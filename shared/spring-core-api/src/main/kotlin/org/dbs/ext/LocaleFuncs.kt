package org.dbs.ext

import org.dbs.application.core.service.funcs.Patterns.LOCALE_PATTERN
import java.util.*

object LocaleFuncs {

    fun String.isValidLocale() = LOCALE_PATTERN.matcher(this).matches()

    fun String.locale(): Locale = Locale(this.substring(0, 2), this.substring(3, 5))

}
