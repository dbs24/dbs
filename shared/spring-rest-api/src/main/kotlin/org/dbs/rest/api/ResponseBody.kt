package org.dbs.rest.api

import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.IntFuncs.thereX
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.VERSION_1_0_0
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.api.enums.RestOperCodeEnum.Companion.badResponse
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_OK
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_THROWABLE_EXCEPTION
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_ERROR
import org.dbs.spring.core.api.EntityInfo
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.Field
import org.dbs.validator.WarnInfo
import java.io.Serializable
import java.util.*

open class ResponseBody<T : EntityInfo> : Logging, Serializable {
    var code: RestOperCodeEnum = OC_UNKNOWN_ERROR
    var message: String? = STRING_NULL
    var error: String? = STRING_NULL
    var version: String = VERSION_1_0_0
    private var isCompleted: Boolean = false
    var createdEntity: T? = null
    val errors = createCollection<ErrorInfo>()
    var errorsCount: Int = 0

    @get:JsonIgnore
    override val logger: KotlinLogger get() = super.logger

    @JsonIgnore
    private var warnings = createCollection<WarnInfo>()
    var execTimeMillis = -1

    fun containErrors() = (badResponse.test(code)) || (haveErrors)

    fun assignException(throwable: Throwable) {
        code = OC_THROWABLE_EXCEPTION
        message = OC_THROWABLE_EXCEPTION.getValue()
        error = throwable.message ?: error
    }

    private fun addErrorInfo(errorInfo: ErrorInfo) = errors.run {
        add(errorInfo)
        message?.let {
            // message is not empty
        } ?: {
            message = errorInfo.errorMsg
            error = errorInfo.errorMsg
            createdEntity = null
        }
        size
    }

    fun addErrorInfo(restOperCodeEnum: RestOperCodeEnum, error: Error, field: Field, errorMsg: String) =
        addErrorInfo(ErrorInfo.create(error, field, errorMsg)).also {
            if (code == OC_OK || code == OC_UNKNOWN_ERROR) {
                code = restOperCodeEnum
            }
        }

    fun addWarnInfo(warnInfo: WarnInfo) = warnings.add(warnInfo).run { warnings.size }

    fun complete() {
        isCompleted = true
    }

    fun dropCreatedEntity() {
        createdEntity = null
    }

    fun assignErrors(errors: Collection<ErrorInfo>) {
        this.errors.addAll(errors)
        complete()
        errors.let { e ->
            if (!e.isEmpty()) {
                message ?: {
                    message = e.stream().findFirst().orElseThrow().errorMsg.uppercase(Locale.getDefault())
                }
                error = message
                logger.warn(
                    "${e.size.thereX()} error(s): $e"
                )
            }
        }
    }

    @get:JsonIgnore
    val haveErrors: Boolean
        get() = errors.isNotEmpty()

    @get:JsonIgnore
    val haveNoErrors: Boolean
        get() = errors.isEmpty()

    override fun toString() = "code='${code}', message='$message', error='$error', " +
            "errors=$errors, ${javaClass.simpleName}($createdEntity), took $execTimeMillis ms"


    companion object {
        @java.io.Serial
        private const val serialVersionUID: Long = 10000L
    }
}
