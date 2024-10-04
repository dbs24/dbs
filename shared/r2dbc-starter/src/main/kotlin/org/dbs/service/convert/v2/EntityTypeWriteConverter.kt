package org.dbs.service.convert.v2

import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.entity.core.v2.type.EntityType
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class EntityTypeWriteConverter : Converter<EntityType, EntityTypeId> {
    override fun convert(entityType: EntityType) = entityType.entityTypeId
}
