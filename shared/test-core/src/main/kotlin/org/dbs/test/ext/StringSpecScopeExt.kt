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
import java.util.regex.Pattern
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy


val invalidPatternValues: Map<Pattern, Any> by lazy {
    hashMapOf(
        USER_FIRST_NAME_PATTERN to "John-James".repeat(81),
        USER_LAST_NAME_PATTERN to "Doe Smith".repeat(81),
        PHONE_PATTERN to "112",
        EMAIL_PATTERN to "fakedEmail",
        LOGIN_PATTERN to "faked#Login",
        PASSWORD_PATTERN to "faked#password",
    )
}

fun <T: DomainCommand> StringSpecTestFactoryConfiguration.generateDefValidationTestsWithFail(
    testName: String,
    function: KFunction<*>,
    contract: ValidationPattern<T>) {

    suspend fun buildArguments(invalidValue: Any, fieldValidationRule: FieldValidationRule<*>) {

        val arguments = mutableMapOf<KParameter, Any>()
        arguments[function.parameters[0]] = contract
        val expectedError = Error.INVALID_ATTR_PATTERN_MISMATCH to fieldValidationRule.field
        arguments[function.parameters[1]] = arrayOf(expectedError)
        val targetParam = function.parameters.drop(2).firstOrNull { param ->
            param.name == fieldValidationRule.property.name
        } ?: throw IllegalArgumentException("Параметр ${fieldValidationRule.property.name} не найден в функции")
        arguments[targetParam] = invalidValue
        require(targetParam.name == fieldValidationRule.property.name) {"property names mismatch"}
        function.callSuspendBy(arguments)

    }

    contract.rules.forEach {
        val fullTestName = "$testName with invalid '${it.property.name}'"
        fullTestName {

            if (it.minMax.first > 0) {
                buildArguments("z".repeat(it.minMax.first-1), it)
            }

            if (it.minMax.second > 0) {
                buildArguments("z".repeat(it.minMax.second+1), it)
            }

            val invalidValue = invalidPatternValues[it.pattern] ?: error("Pattern not found in bad value collection: " +
                    "property: '${it.property.name}', pattern: '${it.pattern}', testName: '$fullTestName'")

            buildArguments(invalidValue, it)

        }
    }
}
