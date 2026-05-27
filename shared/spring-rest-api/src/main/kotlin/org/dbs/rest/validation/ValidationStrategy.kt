package org.dbs.rest.validation

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.enums.I18NEnum
import org.dbs.enums.I18NEnum.MANDATORY_FIELD_MISSING
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.validator.Error
import org.dbs.validator.Error.INVALID_ATTR_PATTERN_MISMATCH
import org.dbs.validator.ErrorInfo
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
import org.springframework.beans.factory.SmartInitializingSingleton
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors.newFixedThreadPool
import java.util.regex.Pattern
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

interface ValidationStrategy<T : DomainCommand> : Logging, SmartInitializingSingleton {

    val supportedClass: KClass<T>
    val rules: Collection<FieldValidationRule<T>>
    val requireAllFieldsValidated: Boolean get() = true

    override fun afterSingletonsInstantiated() {
        verifyRulesCompleteness()
    }

    fun verifyRulesCompleteness() {
        if (!requireAllFieldsValidated) return

        val constructorParamNames = supportedClass.primaryConstructor
            ?.parameters
            ?.mapNotNull { it.name }
            ?.toSet() ?: emptySet()

        // Оптимизация: Сборка множества за один проход вместо многократного маппинга
        val coveredPropertyNames = rules.mapTo(HashSet(rules.size)) { it.property.name }
        val missingProperties = constructorParamNames - coveredPropertyNames

        require(missingProperties.isEmpty()) {
            "Validation strategy for ${supportedClass.simpleName} is incomplete. " +
                    "Missing validation rules for fields: $missingProperties"
        }

        // Оптимизация: Поиск дубликатов сгруппирован в один проход, убраны лишние дублирующие проверки в конце
        val duplicates = rules.groupBy { it.property }.filterValues { it.size > 1 }

        require(duplicates.isEmpty()) {
            val className = " ${this::class.simpleName} (${supportedClass.simpleName})"
            val details = duplicates.entries.joinToString(separator = "; ") { (property, list) ->
                "Property '${property.name}' appears ${list.size} times"
            }
            "Duplicate validation rules found for class '$className': $details"
        }
    }

    fun validateInternal(dto: T, action: suspend (MutableCollection<ErrorInfo>) -> Unit) {
        createCollection<ErrorInfo>().apply {

            for (rule in rules) {
                val rawValue = rule.getter(dto)
                val stringValue = rule.extractString(rawValue)

                // Оптимизация: Свойства minMax кэшируются локально, убирая повторные обращения к геттерам Pair в цикле
                val (minSize, maxSize) = rule.minMax

                if (!rule.isOptional && stringValue.isNullOrBlank()) {
                    this.add(
                        create(
                            Error.MANDATORY_FIELD_MISSING, rule.field,
                            "${rule.property.name}: ${findI18nMessage(MANDATORY_FIELD_MISSING)}"
                        )
                    )
                    continue
                }

                if (!stringValue.isNullOrBlank()) {
                    var isValid = true
                    val length = stringValue.length

                    if (minSize > 0 && length < minSize) {
                        isValid = false
                        this.add(
                            create(
                                INVALID_ATTR_PATTERN_MISMATCH, rule.field,
                                "${rule.property.name}: " +
                                        "${findI18nMessage(I18NEnum.VALUE_DOES_NOT_MATCH_FORMAT)}: '$rawValue' (minSize: $minSize)"
                            )
                        )
                    }

                    if (maxSize in 1..<length) {
                        isValid = false
                        this.add(
                            create(
                                INVALID_ATTR_PATTERN_MISMATCH, rule.field,
                                "${rule.property.name}: " +
                                        "${findI18nMessage(I18NEnum.VALUE_DOES_NOT_MATCH_FORMAT)}: '$rawValue'  (maxSize: $maxSize)"
                            )
                        )
                    }

                    if (isValid && !rule.pattern.matcher(stringValue).matches()) {
                        this.add(
                            create(
                                INVALID_ATTR_PATTERN_MISMATCH, rule.field,
                                "${rule.property.name}: ${findI18nMessage(I18NEnum.VALUE_DOES_NOT_MATCH_FORMAT)}: '$rawValue'"
                            )
                        )
                    }
                }
            }

            if (isEmpty()) runBlocking(validationDispatcher) {
                action(this@apply)
            }

            if (isNotEmpty()) {
                logger.error { "Validation failure for ${supportedClass.simpleName}: $size error${if (size > 1) "s" else ""} found" }
                throw ValidationException(this)
            }
        }

    }

    fun validate(request: T) {
        logger.info { "validate request: $request" }
        validateInternal(request) { errors ->
            errors.apply {
                if (isNotEmpty()) {
                    logger.error { "Validation failure for ${supportedClass.simpleName}: $size error${if (size > 1) "s" else ""} found" }
                }
            }
        }
    }

    infix fun <T : DomainCommand> KProperty1<T, *>.matches(fld: Pair<Pattern, Field>) =
        FieldValidationRule(
            property = this,
            pattern = fld.first,
            isOptional = this.returnType.isMarkedNullable,
            field = fld.second,
            getter = { this.get(it) }
        )

    companion object {
        private val validationDispatcher by lazy {
            newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
            ).asCoroutineDispatcher()
        }
    }

}

data class FieldValidationRule<T : DomainCommand>(
    val property: KProperty1<T, *>,
    val pattern: Pattern,
    val isOptional: Boolean,
    val field: Field,
    val getter: (T) -> Any?,
) : Logging {

    companion object {
        // Кэш для хранения вычисленных диапазонов minMax по строке паттерна
        private val rangeCache = ConcurrentHashMap<String, Pair<Int, Int>>()
    }

    val minMax: Pair<Int, Int> by lazy {
        val patternString = pattern.pattern()
        rangeCache.computeIfAbsent(patternString) { key ->
            extractRange(key).also { range ->
                logger.info { "Calculated and cached pattern: $key, minMax range: $range" }
            }
        }
    }

    fun extractString(obj: Any?): String? = when (obj) {
        null -> null
        is String -> obj
        else -> obj.toString()
    }

    fun extractRange(p: String): Pair<Int, Int> {
        var depth = 0
        var start = -1
        var found = false
        var min = 0
        var max = 0

        for (i in p.indices) {
            when (p[i]) {
                '(' -> depth++
                ')' -> if (depth > 0) depth--
                '{' -> if (depth == 0) start = i
                '}' -> if (start != -1 && depth == 0) {
                    if (found) return 0 to 255

                    // Поиск запятой напрямую в исходной строке, убирая substring()
                    var commaIdx = -1
                    for (j in (start + 1)..<i) {
                        if (p[j] == ',') {
                            commaIdx = j
                            break
                        }
                    }

                    if (commaIdx == -1) {
                        val v = parseTrimmedInt(p, start + 1, i) ?: return 0 to 255
                        min = v; max = v
                    } else {
                        val a = parseTrimmedInt(p, start + 1, commaIdx) ?: return 0 to 255
                        val b = parseTrimmedInt(p, commaIdx + 1, i) ?: return 0 to 255
                        min = a; max = b
                    }
                    found = true
                    start = -1
                }
            }
        }
        return if (found) min to max else 0 to 255
    }

    private fun parseTrimmedInt(s: String, start: Int, end: Int): Int? {
        var left = start
        var right = end - 1
        while (left <= right && s[left] <= ' ') left++
        while (right >= left && s[right] <= ' ') right--
        if (left > right) return null

        var res = 0
        for (i in left..right) {
            val digit = s[i] - '0'
            if (digit !in 0..9) return null
            res = res * 10 + digit
        }
        return res
    }
}
