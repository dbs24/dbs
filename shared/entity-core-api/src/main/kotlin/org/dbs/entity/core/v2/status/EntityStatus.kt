package org.dbs.entity.core.v2.status

import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.EntityTypeId


typealias EntityStatusId = Int
typealias EntityStatusName = String

typealias AllowedStatusesRoutes = Map<EntityStatusEnum, Collection<EntityStatusEnum>>
typealias AllowedMapRoutes = Map<EntityTypeEnum, AllowedStatusesRoutes>

sealed interface EntityStatus {
    val entityStatusId: EntityStatusId
    val entityType: EntityTypeId
    val entityStatusName: EntityStatusName
}
