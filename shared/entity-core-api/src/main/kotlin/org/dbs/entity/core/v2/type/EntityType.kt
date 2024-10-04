package org.dbs.entity.core.v2.type

import org.dbs.entity.core.v2.consts.*
import org.dbs.entity.core.v2.consts.EntityV2Consts.ET_RATE
import org.dbs.entity.core.v2.status.EntityStatus
import org.dbs.entity.core.v2.status.EntityStatusId
import org.dbs.entity.core.v2.status.EntityStatusName

sealed interface EntityType {
    val entityTypeId: EntityTypeId
    val entityTypeName: EntityTypeName
    val module: Application
    val existsEntityStatuses: Collection<EntityStatus>
}
