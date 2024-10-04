package org.dbs.analyst.dao.convert

import org.dbs.player.enums.PlayerStatusEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class PlayerStatusEnumConverter : Converter<PlayerStatusEnum, Int> {
    override fun convert(enum: PlayerStatusEnum) = enum.getCode()
}
