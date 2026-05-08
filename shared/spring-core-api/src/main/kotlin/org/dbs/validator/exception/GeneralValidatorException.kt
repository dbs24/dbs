package org.dbs.validator.exception

import org.dbs.application.core.exception.InternalAppException
import org.dbs.validator.ErrorInfo

class GeneralValidatorException(message: String) : InternalAppException(message)
class ValidationException(val errors: Collection<ErrorInfo>) : InternalAppException()
