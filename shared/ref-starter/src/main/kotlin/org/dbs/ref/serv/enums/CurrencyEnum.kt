package org.dbs.ref.serv.enums

import org.dbs.application.core.service.funcs.TestFuncs.selectFrom
import org.dbs.exception.UnknownEnumException

enum class CurrencyEnum(
    private val currencyId: Int,
    private val currencyIso: String,
    private val currencyName: String
) {
    CAD(124, "CAD", "Canadian Dollar"),
    KZT(398, "KZT", "Kazah Tenge"),
    RUB(643, "RUB", "Russian Ruble"),
    BYN(933, "BYN", "Belarussian Ruble"),
    USD(840, "USD", "Dollar USA"),
    EUR(978, "EUR", "Euro"),
    GBP(826, "GBP", "Pound Sterling"),
    JPY(392, "JPY", "Yen"),
    CHF(756, "CHF", "Swiss Franc"),
    CNY(156, "CNY", "Yuan Renminbi"),
    ;

    companion object {
        //==================================================================================================================
        //@get:Synchronized

        fun getEnum(currencyId: Int): CurrencyEnum =
            entries.find { it.currencyId == currencyId }
                ?: throw UnknownEnumException("currencyId not found ($$currencyId)")

        fun getEnum(currencyIso: String): CurrencyEnum =
            entries.find { it.currencyIso == currencyIso }
                ?: throw UnknownEnumException("currencyIso not found ($$currencyIso)")


        fun isExistCurrency(currencyIso: String) = entries.any { it.currencyIso == currencyIso }

        fun isExistEnum(id: Int) = entries.any { it.currencyId == id }

        val currencyIsos by lazy { entries.map(CurrencyEnum::currencyIso) }

        fun getRandomCurrencyIso() = selectFrom(currencyIsos)
    }

    fun getCurrencyName() = this.currencyName

    fun getCurrencyIso() = this.currencyIso

    fun getCurrencyId() = this.currencyId
}
