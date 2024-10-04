package org.dbs.application.core.service.funcs

object BoolFuncs {
    fun Boolean.all() = if (this) 0 else 1
}
