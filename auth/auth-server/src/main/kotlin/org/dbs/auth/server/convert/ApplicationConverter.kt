package org.dbs.auth.server.convert

import org.dbs.auth.server.enums.ApplicationEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class ApplicationConverter : Converter<Int, ApplicationEnum> {
    override fun convert(applicationId: Int): ApplicationEnum = ApplicationEnum.getEnum(applicationId)
}
