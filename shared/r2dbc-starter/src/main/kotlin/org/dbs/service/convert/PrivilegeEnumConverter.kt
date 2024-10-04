package org.dbs.service.convert

import org.dbs.entity.security.enums.PrivilegeEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class PrivilegeEnumConverter : Converter<PrivilegeEnum, Int> {
    override fun convert(privilegeEnum: PrivilegeEnum): Int = privilegeEnum.getCode()
}
