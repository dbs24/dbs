package org.dbs.ref.serv.enums.convert

import org.dbs.consts.CountryIsoCode
import org.dbs.ref.serv.enums.CountryEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class CountryIsoConverter : Converter<CountryIsoCode, CountryEnum> {
    override fun convert(countryIsoCode: CountryIsoCode): CountryEnum = CountryEnum.getEnum(countryIsoCode)
}
