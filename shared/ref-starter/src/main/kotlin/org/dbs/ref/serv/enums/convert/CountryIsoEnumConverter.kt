package org.dbs.ref.serv.enums.convert

import org.dbs.consts.CountryIsoCode
import org.dbs.ref.serv.enums.CountryEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class CountryIsoEnumConverter : Converter<CountryEnum, CountryIsoCode> {
    override fun convert(countryEnum: CountryEnum) = countryEnum.getCountryIso()
}
