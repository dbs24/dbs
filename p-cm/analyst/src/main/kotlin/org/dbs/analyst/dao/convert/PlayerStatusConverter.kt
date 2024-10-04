package org.dbs.analyst.dao.convert

import org.dbs.player.enums.PlayerStatusEnum
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class PlayerStatusConverter : Converter<Int, PlayerStatusEnum> {
    override fun convert(id: Int) = PlayerStatusEnum.getEnum(id)
}
