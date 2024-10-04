package org.dbs.service.convert

import org.dbs.entity.security.enums.PrivilegeGroupEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class PrivilegeGroupEnumConverter : Converter<PrivilegeGroupEnum, Int> {
    override fun convert(privilegeGroupEnum: PrivilegeGroupEnum) = privilegeGroupEnum.getCode()
}
