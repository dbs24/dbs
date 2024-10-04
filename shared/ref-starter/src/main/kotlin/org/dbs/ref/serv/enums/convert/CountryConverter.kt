package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.CountryEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class CountryConverter : Converter<Int, CountryEnum> {
    override fun convert(countryId: Int): CountryEnum = CountryEnum.getEnum(countryId)
}
