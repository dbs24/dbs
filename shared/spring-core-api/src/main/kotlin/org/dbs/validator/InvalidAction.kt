package org.dbs.validator

fun interface InvalidAction {
    fun invalidAction(errors: Collection<ErrorInfo>)
}
