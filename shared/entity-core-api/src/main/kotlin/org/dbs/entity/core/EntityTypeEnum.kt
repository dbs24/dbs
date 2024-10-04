package org.dbs.entity.core

import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.entity.core.v2.consts.ActionName
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.entity.core.v2.consts.EntityTypeName
import org.dbs.entity.core.v2.consts.EntityV2Consts.CC_RATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.ET_RATE
import org.dbs.entity.core.v2.status.EntityStatusId
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.entity.core.v2.type.Application
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses

interface EntityTypeEnum {
    val entityTypeId: EntityTypeId
    val entityTypeName: EntityTypeName
    val module: Application
    val existsEntityStatuses: Collection<EntityStatusEnum>
        get() = entityStatuses.filter { it.entityType.entityTypeId == entityTypeId  }
}

interface EntityStatusEnum {

    val statusCode: Int
    val entityStatusId: EntityStatusId
        get() = entityType.entityTypeId * ET_RATE + statusCode
    val entityType: EntityTypeEnum
    val entityStatusName: EntityStatusName
}

interface EntityActionEnum {

    val actionCode: Int
    val actionCodeId: ActionCodeId
        get() = entityType.entityTypeId * ET_RATE * 10 + actionCode
    val actionName: ActionName
    val entityType: EntityTypeEnum
}

interface EntityCacheKeyEnum {

    val keyCode: Int
     val cacheKeyCodeId: Int
        get() = entityType.entityTypeId * CC_RATE + keyCode
    val cacheCode: ActionName
    val entityType: EntityTypeEnum
}