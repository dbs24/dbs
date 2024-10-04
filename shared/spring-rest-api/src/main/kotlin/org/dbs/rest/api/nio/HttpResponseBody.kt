package org.dbs.rest.api.nio

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.IntFuncs.thereX
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_ENTITY_ATTRS
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_OK
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.Field
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import java.io.Serializable

abstract class HttpResponseBody<T : ResponseDto>(private val requestId: RequestId) : Logging, Serializable {
    lateinit var responseCode: RestOperCodeEnum // = OC_UNEXPECTED_EXCEPTION
    lateinit var message: String
    var error = STRING_NULL
    private val isCompleted = LateInitVal<Boolean>()
    var responseEntity: T? = null
    val errors by lazy { createCollection<ErrorInfo>() }

    @get:JsonIgnore
    override val logger: KotlinLogger get() = super.logger

    val errorsCount: Int get() = errors.size
    var execTimeMillis = -1

    @JsonIgnore
    val contentType: MediaType = APPLICATION_JSON

    init {
        logger.debug { "create response: ${javaClass.simpleName}, requestId: $requestId" }
    }

    //==================================================================================================================
    @JsonIgnore
    fun haveErrors() =
        (responseCode != if (::responseCode.isInitialized) responseCode else OC_OK) || (errors.isNotEmpty())

    @JsonIgnore
    fun haveNoErrors() = !haveErrors()

    private fun addErrorInfo(errorInfo: ErrorInfo) = errors.run {
        add(errorInfo)
        logger.warn { "### add custom error: [$errorInfo] (${this@HttpResponseBody.javaClass.canonicalName})" }

        if (!::message.isInitialized) {
            message = errorInfo.errorMsg
        }

        error?.let {
            // message is not empty
        } ?: {
            error = errorInfo.errorMsg
            responseEntity = null
        }
        size
    }

    fun addErrorInfo(restOperCodeEnum: RestOperCodeEnum, error: Error, field: Field, errorMsg: String) =
        addErrorInfo(ErrorInfo.create(error, field, errorMsg)).also {
            if (!::responseCode.isInitialized) {
                responseCode = restOperCodeEnum
            } else if (responseCode == OC_OK) {
                responseCode = restOperCodeEnum
                message = errorMsg
            }
        }

    fun complete() {
        isCompleted.init(true)
    }

    private fun setErrorMessage(errorMsg: String) = error?.let {} ?: { error = errorMsg }

    fun assignErrors(errors: Collection<ErrorInfo>) {
        this.errors.addAll(errors)
        complete()
        errors.let { e ->
            if (!e.isEmpty()) {
                responseCode = OC_INVALID_ENTITY_ATTRS

                if (!::message.isInitialized) {
                    message = e.stream().findFirst().orElseThrow().errorMsg.uppercase()
                }

                setErrorMessage(message)

                logger.warn { "${e.size.thereX()} error(s): $e" }
            }
        }
    }

    fun toString2() =
        "code='${responseCode}', message='$message', error='$error', errors=$errors, execTimeMillis=$execTimeMillis, " +
                "${javaClass.simpleName}($responseEntity), requestId=$requestId"

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 1000L
    }
}
