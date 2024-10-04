package org.dbs.ref.serv.enums.convert

import org.dbs.consts.CurrencyId
import org.dbs.ref.serv.enums.CurrencyEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class CurrencyEnumConverter : Converter<CurrencyEnum, CurrencyId> {
    override fun convert(currencyEnum: CurrencyEnum): CurrencyId = currencyEnum.getCurrencyId()
}
