package org.dbs.rest.api.exception

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.ext.IncidentSource
import org.dbs.ext.SpringFuncs.registryIncidentEvent
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler(
    private val applicationEventPublisher: ApplicationEventPublisher): Logging {

    private val isDevelopmentMode by lazy { System.getProperty("spring.profiles.active") == "dev" }

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleValidationException(ex: ValidationException): ValidationErrorResponse =
        ValidationErrorResponse(
            type = "RESTful exception",
            title = BAD_REQUEST.toString(),
            status = BAD_REQUEST.value(),
            detail = "Validation exception",
            errors = ex.errors)

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(BAD_REQUEST)
    fun handleGenericIllegalArgumentException(
        throwable: IllegalArgumentException,
        request: ServerHttpRequest
    ): ValidationErrorResponse {

        val incidentMsg =  applicationEventPublisher.registryIncidentEvent(
            throwable = throwable,
            path = request.uri.toString(),
            IncidentSource.IS_REST,
        )

        return ValidationErrorResponse(
            type = "RESTful exception",
            title = BAD_REQUEST.toString(),
            status = BAD_REQUEST.value(),
            detail = if (isDevelopmentMode) {
                "Unexpected error: ${throwable.message}"
            } else {
                incidentMsg
            },
            errors = listOf(
                create(
                    error = Error.BAD_REQUEST_ERROR,
                    field = Field.UNKNOWN_FIELD,
                    errorMsg = if (isDevelopmentMode) {
                        throwable.message ?: "Unknown bad request error"
                    } else incidentMsg
                )
            ),
        )
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleGenericException(
        throwable: Throwable,
        request: ServerHttpRequest
    ): ValidationErrorResponse {

        val incidentMsg =  applicationEventPublisher.registryIncidentEvent(
            throwable = throwable,
            path = request.uri.toString(),
            IncidentSource.IS_REST,
        )

        return ValidationErrorResponse(
            type = "RESTful exception",
            title = INTERNAL_SERVER_ERROR.toString(),
            status = INTERNAL_SERVER_ERROR.value(),
            detail = if (isDevelopmentMode) {
                "Unexpected error: ${throwable.message}"
            } else {
                incidentMsg
            },
            errors = listOf(
                create(
                    error = Error.GENERAL_ERROR,
                    field = Field.UNKNOWN_FIELD,
                    errorMsg = if (isDevelopmentMode) {
                        throwable.message ?: "Unknown error"
                    } else incidentMsg
                )
            ),
        )
    }
}

data class ValidationErrorResponse(
    val type: String = "default errorType",
    val title: String = "Validation Failed",
    val status: Int = BAD_REQUEST.value(),
    val detail: String = "One or more fields failed validation.",
    val errors: Collection<ErrorInfo>,
    val timeStamp: Long = System.currentTimeMillis()
)

