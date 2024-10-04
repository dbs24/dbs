package org.dbs.service.convert.v2

import org.dbs.entity.core.v2.status.EntityStatus
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class EntityStatusWriteConverter : Converter<EntityStatus, EntityTypeId> {
    override fun convert(entityStatus: EntityStatus) = entityStatus.entityStatusId
}
