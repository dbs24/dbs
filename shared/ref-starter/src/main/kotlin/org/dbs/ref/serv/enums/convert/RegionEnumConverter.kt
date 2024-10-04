package org.dbs.ref.serv.enums.convert

import org.dbs.ref.serv.enums.RegionEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class RegionEnumConverter : Converter<RegionEnum, Int> {
    override fun convert(regionEnum: RegionEnum) = regionEnum.getRegionId()
}
