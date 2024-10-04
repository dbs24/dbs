package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.GenderEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class GenderEnumConverter : Converter<GenderEnum, String> {
    override fun convert(countryEnum: GenderEnum) = countryEnum.getCode()
}
