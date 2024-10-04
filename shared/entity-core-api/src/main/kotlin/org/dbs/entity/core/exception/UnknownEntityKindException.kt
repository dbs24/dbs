package org.dbs.entity.core.exception

import org.dbs.application.core.exception.InternalAppException
import org.dbs.consts.ErrMsg

class UnknownEntityKindException(message: ErrMsg) : InternalAppException(message)
