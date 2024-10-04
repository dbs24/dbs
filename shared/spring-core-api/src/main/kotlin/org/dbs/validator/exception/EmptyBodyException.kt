package org.dbs.validator.exception

import org.dbs.application.core.exception.InternalAppException

class EmptyBodyException(message: String) : InternalAppException(message)
