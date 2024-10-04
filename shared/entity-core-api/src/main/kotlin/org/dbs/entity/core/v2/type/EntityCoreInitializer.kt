package org.dbs.entity.core.v2.type

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.entity.core.v2.status.EntityStatusId
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityTypes

interface EntityCoreInitializer {
    companion object {

        object EntityCore {
            val entityTypes by lazy { createCollection<EntityTypeEnum>() }
            val entityStatuses by lazy { createCollection<EntityStatusEnum>() }
            val entityActionEnums by lazy { createCollection<EntityActionEnum>() }
            val cacheKeys by lazy { createCollection<EntityCacheKeyEnum>() }
        }

        // default entity cache id key
        //val CC_ENTITY_ID = createCacheKey(1, "ENTITY.ID")
        // cache keys

        fun findEntityType(entityTypeId: EntityTypeId): EntityTypeEnum = let {
            entityTypes.find { it.entityTypeId == entityTypeId } ?: error("Unknown entity typeId (${entityTypeId})")
        }

        fun findEntityStatus(entityStatusId: EntityStatusId): EntityStatusEnum = let {
            entityStatuses.find { it.entityStatusId == entityStatusId }
                ?: error("Unknown entity statusId (${entityStatusId})")
        }
    }
}
