package org.dbs.application.core.exception

import java.lang.String.format
import java.util.*

open class InternalAppException : RuntimeException {
    constructor() : super()
    constructor(aErrMsg: String) : super(aErrMsg)

    companion object {

        fun getSuppressedErrMessage(throwable: Throwable) = StringBuilder(1024).run {

            val msgMask = "%s: '%s'; "
            append(format(msgMask, throwable.javaClass.simpleName, throwable.message))
            throwable.cause?.let { th ->
                append(
                    format(
                        "Cause: $msgMask",
                        th.javaClass.simpleName,
                        th.message
                    )
                )
            }

            throwable.message?.let {

                throwable.fillInStackTrace()?.let { th ->
                    append(format(msgMask, th.javaClass.simpleName, th.message))
                }
                if (throwable.suppressed.isNotEmpty()) {
                    append(format("getSuppressed size: %d;", throwable.suppressed.size))
                    Arrays.stream(throwable.suppressed)
                        .forEach { th -> append(format(msgMask, th.javaClass.simpleName, th.message)) }
                }
            }

            if (isEmpty()) {
                append("No Exception details applied \n")
            }
            toString()
        }
    }
}
