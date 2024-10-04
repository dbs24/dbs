package org.dbs.rest.dto

data class CurrencyDto(
    val currencyId: Int,
    val currencyIso: String,
    val currencyName: String
)