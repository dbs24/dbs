package org.dbs.service.convert

import org.dbs.entity.security.enums.PrivilegeEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class PrivilegeConverter : Converter<Int, PrivilegeEnum> {
    override fun convert(privilegeId: Int): PrivilegeEnum = PrivilegeEnum.getEnum(privilegeId)
}
