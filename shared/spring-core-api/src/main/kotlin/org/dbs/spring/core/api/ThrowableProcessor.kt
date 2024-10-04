package org.dbs.spring.core.api

fun interface ThrowableProcessor {
    fun processThrowable(throwable: Throwable)
}
