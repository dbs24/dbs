package org.dbs.service.convert

import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@ReadingConverter
class EntityStatusReadConverter : Converter<Int, EntityStatusEnum> {
    override fun convert(source: Int): EntityStatusEnum =
        EntityCoreInitializer.Companion.findEntityStatus(source)
}

@WritingConverter
class EntityStatusWriteConverter : Converter<EntityStatusEnum, Int> {
    override fun convert(source: EntityStatusEnum): Int = source.entityStatusId
}
