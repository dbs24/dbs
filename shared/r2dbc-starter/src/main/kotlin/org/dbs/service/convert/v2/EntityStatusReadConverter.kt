package org.dbs.service.convert.v2

import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.status.EntityStatusId
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.findEntityStatus
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class EntityStatusReadConverter : Converter<EntityStatusId, EntityStatusEnum> {
    override fun convert(entityStatusId: EntityStatusId): EntityStatusEnum = findEntityStatus(entityStatusId)
}
