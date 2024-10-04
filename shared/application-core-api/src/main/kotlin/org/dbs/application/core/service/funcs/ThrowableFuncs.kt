package org.dbs.application.core.service.funcs

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.ThrowableConsts.SB_THROWABLE_MSG_LENGTH

object ThrowableFuncs {

    fun Throwable.getExtendedErrMessage() = this.let { throwable ->
        StringBuilder(SB_THROWABLE_MSG_LENGTH).run {
            var errCause = EMPTY_STRING
            throwable.let {
                throwable.cause?.let { cause -> errCause = "\n(${cause.javaClass.name}: ${cause.message})" }
                append("${throwable.javaClass.name}: ${throwable.message}${errCause}")
            }
            if (isEmpty()) {
                append("No Exception details applied \n")
            }
            toString()
        }
    }

}
