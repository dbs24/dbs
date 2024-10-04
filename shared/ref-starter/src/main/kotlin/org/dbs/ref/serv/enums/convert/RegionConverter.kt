package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.RegionEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class RegionConverter : Converter<Int, RegionEnum> {
    override fun convert(regionId: Int): RegionEnum = RegionEnum.getEnum(regionId)
}
