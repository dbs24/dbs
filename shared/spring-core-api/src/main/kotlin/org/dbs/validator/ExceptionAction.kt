package org.dbs.validator

fun interface ExceptionAction {
    fun processException(throwable: Throwable?)
}
