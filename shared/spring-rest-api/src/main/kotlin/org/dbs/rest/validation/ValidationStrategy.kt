package org.dbs.rest.validation

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.enums.I18NEnum
import org.dbs.enums.I18NEnum.MANDATORY_FIELD_MISSING
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
import org.springframework.beans.factory.SmartInitializingSingleton
import java.util.regex.Pattern
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor

interface ValidationStrategy<T : DomainCommand> : Logging, SmartInitializingSingleton {

    val supportedClass: KClass<T>

    val rules: Collection<FieldValidationRule<T>>

    val requireAllFieldsValidated: Boolean
        get() = true

    override fun afterSingletonsInstantiated() {
        verifyRulesCompleteness()
    }

    fun verifyRulesCompleteness() {
        if (!requireAllFieldsValidated) return

        val constructorParamNames = supportedClass.primaryConstructor
            ?.parameters
            ?.mapNotNull { it.name }
            ?.toSet() ?: emptySet()

        val coveredPropertyNames = rules.map { it.property.name }.toSet()
        val missingProperties = constructorParamNames - coveredPropertyNames

        require(missingProperties.isEmpty()) {
            "Validation strategy for ${supportedClass.simpleName} is incomplete. " +
                    "Missing validation rules for fields: $missingProperties"
        }

        val duplicates = rules
            .groupBy { it.property }
            .filter { it.value.size > 1 }

        require(duplicates.isEmpty()) {
            val className = " ${this::class.simpleName} (${supportedClass.simpleName})"
            val details = duplicates.entries.joinToString(separator = "; ") { (property, list) ->
                "Property '${property.name}' appears ${list.size} times"
            }
            "Duplicate validation rules found for class '$className': $details"
        }

        require (coveredPropertyNames.size == coveredPropertyNames.toSet().size) {
            "Validation strategy for ${supportedClass.simpleName} has duplicate rules for fields: " +
                    "${coveredPropertyNames.groupBy { it }.filter { it.value.size > 1 }.keys}"
        }
    }

    fun validateInternal(dto: T, action: suspend (MutableCollection<ErrorInfo>) -> Unit) {
        createCollection<ErrorInfo>().apply {

            for (rule in rules) {

                val rawValue = rule.getter(dto)
                val stringValue = rule.extractString(rawValue)

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
                    if (!rule.pattern.matcher(stringValue).matches()) {
                        this.add(
                            create(
                                Error.INVALID_ATTR_PATTERN_MISMATCH, rule.field,
                                "${rule.property.name}: ${findI18nMessage(I18NEnum.VALUE_DOES_NOT_MATCH_FORMAT)}: '$rawValue'"
                            )
                        )
                    }
                }
            }

            if (isEmpty()) runBlocking {
                action(this@apply)
            }

            if (isNotEmpty()) {
                logger.error { "Validation failure for ${supportedClass.simpleName}: $size error${if (size>1) "s" else ""} found" }
                throw ValidationException(this)
            }
        }
    }

    fun validate(request: T)

    infix fun <T : DomainCommand> KProperty1<T, *>.matches(fld: Pair<Pattern, Field>) =
        FieldValidationRule(
            property = this,
            pattern = fld.first,
            isOptional = this.returnType.isMarkedNullable,
            field = fld.second,
            getter = { this.get(it) } // Прямая ссылка на геттер
        )
}

// Структура, описывающая правило для конкретного свойства
data class FieldValidationRule<T : DomainCommand>(
    val property: KProperty1<T, *>,
    val pattern: Pattern,
    val isOptional: Boolean,
    val field: Field,
    val getter: (T) -> Any?
) {
    fun extractString(obj: Any?): String? = when (obj) {
        null -> null
        is String -> obj
        // Добавьте распаковку ваших доменных примитивов, чтобы избежать toString()
        // is Email -> obj.value
        // is EntityCode -> obj.value
        else -> obj.toString()
    }
}
