package org.dbs.service.convert.v2

import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.findEntityType
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class EntityTypeReadConverter : Converter<EntityTypeId, EntityTypeEnum> {
    override fun convert(entityTypeId: EntityTypeId): EntityTypeEnum = findEntityType(entityTypeId)
}
