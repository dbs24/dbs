package org.dbs.test.ext

import io.kotest.core.spec.style.StringSpecTestFactoryConfiguration
import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PHONE_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_FIRST_NAME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_LAST_NAME_PATTERN
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationPattern
import org.dbs.validator.Error
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy


private val invalidPatternValues: Map<Pattern, Any> by lazy {
    hashMapOf(
        USER_FIRST_NAME_PATTERN to "John-James".repeat(81),
        USER_LAST_NAME_PATTERN to "Doe Smith".repeat(81),
        PHONE_PATTERN to "112",
        EMAIL_PATTERN to "fakedEmail",
        LOGIN_PATTERN to "faked#Login",
        PASSWORD_PATTERN to "faked#password",
    )
}

// Глобальный или companion-object кэш для метаданных функций (минимизирует рефлексию между запусками)
private val functionParametersCache = ConcurrentHashMap<KFunction<*>, FunctionMetadata>()

private class FunctionMetadata(
    val contractParam: KParameter,
    val errorsParam: KParameter,
    val namedParams: Map<String, KParameter>
)

fun <T: DomainCommand> StringSpecTestFactoryConfiguration.generateDefValidationTestsWithFail(
    testName: String,
    function: KFunction<*>,
    contract: ValidationPattern<T>
) {

    val metadata = functionParametersCache.computeIfAbsent(function) { func ->
        val params = func.parameters
        require(params.size >= 2) { "Функция должна иметь как минимум 2 параметра (contract и errors)" }

        // Мапим оставшиеся параметры по имени для мгновенного O(1) поиска
        val namedMap = params.asSequence()
            .drop(2)
            .filter { it.name != null }
            .associateBy { it.name!! }

        FunctionMetadata(
            contractParam = params[0],
            errorsParam = params[1],
            namedParams = namedMap
        )
    }

    // Вспомогательная функция теперь работает без поиска через рефлексию
    suspend fun buildArguments(invalidValue: Any, fieldValidationRule: FieldValidationRule<*>) {
        val propertyName = fieldValidationRule.property.name

        // Быстрый поиск параметра из кэша за O(1)
        val targetParam = metadata.namedParams[propertyName]
            ?: error("Параметр $propertyName не найден в функции ${function.name}")

        val expectedError = Error.INVALID_ATTR_PATTERN_MISMATCH to fieldValidationRule.field

        val arguments = LinkedHashMap<KParameter, Any>(3).apply {
            put(metadata.contractParam, contract)
            put(metadata.errorsParam, arrayOf(expectedError))
            put(targetParam, invalidValue)
        }

        function.callSuspendBy(arguments)
    }

    // 2. Генерация тестов (без накладных расходов на рефлексию внутри)
    contract.rules.forEach { rule ->
        val propName = rule.property.name
        val fullTestName = "$testName with invalid '$propName'"

        fullTestName {
            val min = rule.minMax.first
            if (min > 0) {
                buildArguments("z".repeat(min - 1), rule)
            }

            val max = rule.minMax.second
            if (max > 0) {
                buildArguments("z".repeat(max + 1), rule)
            }

            val invalidValue = invalidPatternValues[rule.pattern]
                ?: error("Pattern not found in bad value collection: property: '$propName', pattern: '${rule.pattern}', testName: '$fullTestName'")

            buildArguments(invalidValue, rule)
        }
    }
}