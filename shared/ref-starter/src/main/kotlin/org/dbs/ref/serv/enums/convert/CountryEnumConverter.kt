package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.CountryEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class CountryEnumConverter : Converter<CountryEnum, Int> {
    override fun convert(countryEnum: CountryEnum) = countryEnum.getCountryId()
}
