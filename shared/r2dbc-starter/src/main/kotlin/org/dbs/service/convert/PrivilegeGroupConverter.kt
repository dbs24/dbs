package org.dbs.service.convert

import org.dbs.entity.security.enums.PrivilegeGroupEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class PrivilegeGroupConverter : Converter<Int, PrivilegeGroupEnum> {
    override fun convert(privlegeGroupId: Int): PrivilegeGroupEnum = PrivilegeGroupEnum.getEnum(privlegeGroupId)
}
