package org.dbs.validator

import org.dbs.application.core.service.funcs.StringFuncs.clearName
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.validator.exception.GeneralValidatorException

abstract class AbstractValidatorService<T> : AbstractApplicationService() {
    //==========================================================================    
    override fun initialize() {
        val className = this.javaClass.simpleName
        logger.trace{"Validator '${className.clearName()}' is activated"}
    }

    abstract fun validate(validatedEntity: T): Collection<ErrorInfo>
    protected open fun assignDefaults(validatedEntity: T) {}
    fun validateThrow(validatedEntity: T) {
        validate(validatedEntity)
            .stream()
            .findFirst()
            .ifPresent { err: ErrorInfo ->
                logger.warn("${this.javaClass.simpleName}: $err")
                throw GeneralValidatorException("Error: $err")
            }
    }

    //==========================================================================
    protected fun createErrMsg(error: Error, errorMsg: String): ErrorInfo = ErrorInfo.create(error, errorMsg)

}
