package org.dbs.auth.server.convert

import org.dbs.auth.server.enums.ApplicationEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class ApplicationEnumConverter : Converter<ApplicationEnum, Int> {
    override fun convert(applicationEnum: ApplicationEnum): Int = applicationEnum.getApplicationId()
}
