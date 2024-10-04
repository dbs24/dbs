package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.CurrencyEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class CurrencyConverter : Converter<Int, CurrencyEnum> {
    override fun convert(currencyId: Int): CurrencyEnum = CurrencyEnum.getEnum(currencyId)
}
