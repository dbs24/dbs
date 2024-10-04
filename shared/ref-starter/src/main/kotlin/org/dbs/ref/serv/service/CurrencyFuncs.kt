package org.dbs.ref.serv.service

import org.dbs.application.core.service.funcs.Patterns.CURRENCY_CODE_PATTERN
import org.dbs.ref.serv.enums.CurrencyEnum.Companion.isExistCurrency

object CurrencyFuncs {
    fun String.isValidCurrencyCode() =
        CURRENCY_CODE_PATTERN.matcher(this).matches() && isExistCurrency(this)
}