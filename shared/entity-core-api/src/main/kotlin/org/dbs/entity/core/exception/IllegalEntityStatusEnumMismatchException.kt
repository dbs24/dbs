package org.dbs.entity.core.exception

import org.dbs.application.core.exception.InternalAppException
import org.dbs.consts.ErrMsg

class IllegalEntityStatusEnumMismatchException(message: ErrMsg) : InternalAppException(message)
