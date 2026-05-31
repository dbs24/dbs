package org.dbs.entity.core

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.ActionCodeId
import org.dbs.consts.EntityTypeId
import org.dbs.entity.core.v2.consts.ActionName
import org.dbs.entity.core.v2.consts.EntityTypeName
import org.dbs.entity.core.v2.consts.EntityV2Consts.CC_RATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.ET_RATE
import org.dbs.entity.core.v2.status.EntityStatusId
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.entity.core.v2.type.Application

interface EntityTypeEnum {
    val entityTypeId: EntityTypeId
    val entityTypeName: EntityTypeName
    val module: Application
    val existsEntityStatuses: Collection<EntityStatusEnum>
        get() = TODO()
}

interface EntityStatusEnum {

    val statusCode: Int
    val entityStatusId: EntityStatusId
        get() = entityType.entityTypeId * ET_RATE + statusCode
    val entityType: EntityTypeEnum
    val entityStatusName: EntityStatusName

    val status: String
        get() = (entityStatusName+"_"+entityType.entityTypeName).uppercase()

    companion object: Logging {
        inline fun <reified T> findStatus(
            status: EntityStatusName,
            entityType: EntityTypeEnum
        ): T? where T : Enum<T>, T : EntityStatusEnum =
            enumValues<T>().firstOrNull { it.entityType == entityType && it.entityStatusName == status }
    }
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