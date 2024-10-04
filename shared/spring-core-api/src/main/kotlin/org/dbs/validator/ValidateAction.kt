package org.dbs.validator

fun interface ValidateAction<T> {
    fun action(t: T)
}
