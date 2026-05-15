package org.dbs.rest.validation

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
import java.util.regex.Pattern
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface ValidationStrategy<T: DomainCommand>: Logging {

    val supportedClass: KClass<T>

    val rules: Collection<FieldValidationRule<T>>

    fun validateInternal(dto: T, action: (MutableCollection<ErrorInfo>) -> Unit) {
        createCollection<ErrorInfo>().apply {

            for (rule in rules) {

                val rawValue = rule.property.get(dto)
                val stringValue = rule.extractString(rawValue)

                if (!rule.isOptional && stringValue.isNullOrBlank()) {
                    this.add(create(Error.MANDATORY_FIELD_MISSING, rule.field,
                        "${rule.property.name}: ${findI18nMessage(MANDATORY_FIELD_MISSING)}"))
                    continue
                }

                if (!stringValue.isNullOrBlank() && rule.pattern != null) {
                    if (!rule.pattern.matches(stringValue)) {
                        this.add(create(Error.INVALID_ATTR_PATTERN_MISMATCH, rule.field,
                            "${rule.property.name}: ${findI18nMessage(I18NEnum.VALUE_DOES_NOT_MATCH_FORMAT)}: '$rawValue'"))
                    }
                }
            }

            if (isEmpty())
              action(this)

            if (isNotEmpty()) {
                logger.error { "Validation failure: $this" }
                throw ValidationException(this)
            }
        }
    }

    fun validate(request: T)

    infix fun <T : DomainCommand> KProperty1<T, *>.matches(fld: Pair<Pattern, Field>) =
        FieldValidationRule(this, fld.first.toRegex(), isOptional = false, field = fld.second)

    infix fun <T : DomainCommand> KProperty1<T, *>.matchesOptional(fld: Pair<Pattern, Field>) =
        FieldValidationRule(this, fld.first.toRegex(), isOptional = true, field = fld.second)

}

// Структура, описывающая правило для конкретного свойства
data class FieldValidationRule<T : DomainCommand>(
    val property: KProperty1<T, *>,
    val pattern: Regex?,
    val isOptional: Boolean, // Разрешено ли значение null / пустая строка
    val field: Field
) {
    // Вспомогательная функция для безопасного извлечения String из кастомных типов данных
    fun extractString(obj: Any?): String? = when (obj) {
        null -> null
        is String -> obj
        // Если у вас EntityCode или Email реализованы как value class или data class с полем value
        else -> obj.toString()
    }
}
