package org.dbs.rest.api.nio

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.IntFuncs.thereX
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
    lateinit var responseCode: RestOperCodeEnum
    lateinit var message: String
    var error = STRING_NULL
    private val isCompleted by lazy { LateInitVal<Boolean>() }
    var responseEntity: T? = null
    var errors: MutableCollection<ErrorInfo> = mutableListOf()

    @get:JsonIgnore
    override val logger: KotlinLogger by lazy { super.logger }

    val errorsCount: Int get() = errors.size
    var execTimeMillis = -1

    @JsonIgnore
    val contentType: MediaType = APPLICATION_JSON

    init {
        logger.debug { "create response: ${javaClass.simpleName}, requestId: $requestId" }
    }

    //==================================================================================================================
    @JsonIgnore
    fun haveErrors() = (runCatching { responseCode }.getOrDefault(OC_OK) != OC_OK) || errors.isNotEmpty()

    @JsonIgnore
    fun haveNoErrors() = !haveErrors()

    private fun addErrorInfo(errorInfo: ErrorInfo) = errors.run {
        add(errorInfo)
        logger.warn { "### add custom error: [$errorInfo] (${this@HttpResponseBody.javaClass.canonicalName})" }

        if (!::message.isInitialized) message = errorInfo.errorMsg

        error.takeIf { it != STRING_NULL } ?: run {
            error = errorInfo.errorMsg
            responseEntity = null
        }
        size
    }

    fun addErrorInfo(restOperCodeEnum: RestOperCodeEnum, error: Error, field: Field, errorMsg: String) =
        addErrorInfo(ErrorInfo.create(error, field, errorMsg)).also {
            if (!::responseCode.isInitialized || responseCode == OC_OK) {
                responseCode = restOperCodeEnum
                message = errorMsg
            }
        }

    fun complete() {
        isCompleted.init(true)
    }

    private fun setErrorMessage(errorMsg: String) {
        error.takeIf { it != STRING_NULL } ?: run { error = errorMsg }
    }

    fun assignErrors(errors: Collection<ErrorInfo>) {
        this.errors.addAll(errors)
        complete()
        errors.takeIf { it.isNotEmpty() }?.let { e ->
            responseCode = OC_INVALID_ENTITY_ATTRS
            if (!::message.isInitialized) {
                message = e.first().errorMsg.uppercase()
            }
            setErrorMessage(message)
            logger.warn { "${e.size.thereX()} error(s): $e" }
        }
    }

    fun toString2() = "code='${runCatching { responseCode }.getOrNull()}', message='${runCatching { message }.getOrNull()}', error='$error', errors=$errors, execTimeMillis=$execTimeMillis, ${javaClass.simpleName}($responseEntity), requestId=$requestId"

    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 1000L
    }
}
