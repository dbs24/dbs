package org.dbs.rest.validation

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.validator.ErrorInfo
import org.dbs.validator.exception.ValidationException
import kotlin.reflect.KClass

interface ValidationStrategy<T: DomainCommand>: Logging {

    val supportedClass: KClass<T>

    fun validateInternal(action: (MutableCollection<ErrorInfo>) -> Unit) {
        createCollection<ErrorInfo>().apply {
            action(this)
            if (isNotEmpty()) {
                logger.error { "Validation failure: $this" }
                throw ValidationException(this)
            }
        }
    }

    fun validate(request: T)

}