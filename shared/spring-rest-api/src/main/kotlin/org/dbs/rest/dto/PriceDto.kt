package org.dbs.rest.dto

import org.dbs.consts.CurrencyIso
import org.dbs.consts.Money

data class PriceDto(
    val value: Money,
    val currencyIso: CurrencyIso
)
