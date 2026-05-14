package org.dbs.rest.api.exception

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler: Logging {

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleValidationException(ex: ValidationException): ValidationErrorResponse =
        ValidationErrorResponse(errors = ex.errors)

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): ValidationErrorResponse {
        // Логируем полный stack trace для отладки
        logger.error(ex) { "Unexpected error occurred: ${ex.message}" }

        // В production не показываем детали исключения клиенту
        val isDevelopment = System.getProperty("spring.profiles.active") == "dev"

        return ValidationErrorResponse(
            type = "unknown type",
            title = "Internal Server Error",
            status = INTERNAL_SERVER_ERROR.value(),
            detail = if (isDevelopment) {
                "Unexpected error: ${ex.message}"
            } else {
                "An unexpected error occurred. Please try again later."
            },
            errors = listOf(
                create(
                    error = Error.GENERAL_ERROR,
                    field = Field.UNKNOWN_FIELD,
                    errorMsg = if (isDevelopment) {
                        ex.message ?: "Unknown error"
                    } else {
                        "Internal server error"
                    }
                )
            )
        )
    }
}

data class ValidationErrorResponse(
    val type: String = "about:blank",
    val title: String = "Validation Failed",
    val status: Int = BAD_REQUEST.value(),
    val detail: String = "One or more fields failed validation.",
    val errors: Collection<ErrorInfo>,
    val timeStamp: Long = System.currentTimeMillis()
)

