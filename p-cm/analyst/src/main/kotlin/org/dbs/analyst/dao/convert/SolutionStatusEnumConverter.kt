package org.dbs.analyst.dao.convert

import org.dbs.player.enums.SolutionStatusEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class SolutionStatusEnumConverter : Converter<SolutionStatusEnum, Int> {
    override fun convert(enum: SolutionStatusEnum) = enum.getCode()
}
