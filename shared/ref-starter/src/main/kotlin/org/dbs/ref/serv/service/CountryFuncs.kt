package org.dbs.ref.serv.service

import org.dbs.application.core.service.funcs.Patterns.COUNTRY_CODE_PATTERN
import org.dbs.ref.serv.enums.CountryEnum.Companion.isExistCountry

object CountryFuncs {
    fun String.isValidCountryCode() = COUNTRY_CODE_PATTERN.matcher(this).matches() && isExistCountry(
        this
    )
}