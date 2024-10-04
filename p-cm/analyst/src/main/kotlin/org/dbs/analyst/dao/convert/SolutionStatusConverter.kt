package org.dbs.analyst.dao.convert

import org.dbs.player.enums.PlayerStatusEnum
import org.dbs.player.enums.SolutionStatusEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class SolutionStatusConverter : Converter<Int, SolutionStatusEnum> {
    override fun convert(id: Int) = SolutionStatusEnum.getEnum(id)
}
