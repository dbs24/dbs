package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.GenderEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class GenderConverter : Converter<String, GenderEnum> {
    override fun convert(genderCode: String): GenderEnum = GenderEnum.getEnum(genderCode.trim())
}
