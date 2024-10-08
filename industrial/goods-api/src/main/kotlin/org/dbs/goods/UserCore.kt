package org.dbs.goods

import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.EntityConsts.EntityStatuses.BANNED
import org.dbs.consts.EntityConsts.EntityStatuses.CLOSED
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.ClosedEntity
import org.dbs.goods.UserCore.EntityStatus.*
import org.dbs.goods.UserCore.EntityTypes.ET_USER
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_CODE
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_ID
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_CREATE_OR_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_UPDATE_PASSWORD
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_ACTUAL
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_BANNED
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_CLOSED
import org.dbs.entity.core.v2.type.Application
import org.dbs.entity.core.v2.type.Application.CHESS
import org.dbs.entity.core.v2.type.Application.INDUSTRIAL
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityTypeExtension.registerAllowedStatusesChanges
import org.dbs.goods.UsersConsts.Names.USER

object UserCore : EntityCoreInitializer {

    // Types
    enum class EntityTypes : EntityTypeEnum {
        ET_USER {
            override val entityTypeId = 100100
            override val entityTypeName = USER
            override val module = INDUSTRIAL
        },
    }

    enum class EntityStatus : EntityStatusEnum {
        ES_USER_ACTUAL {
            override val statusCode = EGS_ACTUAL
            override val entityType = ET_USER
            override val entityStatusName = ACTUAL
        },
        ES_USER_CLOSED {
            override val statusCode = EGS_CLOSED
            override val entityType = ET_USER
            override val entityStatusName = CLOSED
        },
        ES_USER_BANNED {
            override val statusCode = EGS_BANNED
            override val entityType = ET_USER
            override val entityStatusName = BANNED
        }
    }

    enum class UserActionEnum : EntityActionEnum {
        EA_CREATE_OR_UPDATE_USER {
            override val actionCode = EGAS_CREATE_OR_UPDATE
            override val entityType = ET_USER
            override val actionName = "Create or update player"
        },
        EA_UPDATE_USER_STATUS {
            override val actionCode = EGAS_UPDATE
            override val entityType = ET_USER
            override val actionName = "update player status"
        },
        EA_UPDATE_USER_PASSWORD {
            override val actionCode = EGAS_UPDATE_PASSWORD
            override val entityType = ET_USER
            override val actionName = "update player password"
        }
    }

    //
    // cache keys
    enum class CacheKeyUserEnum : EntityCacheKeyEnum {
        CC_USER_ID {
            override val keyCode = CC_ID
            override val entityType = ET_USER
            override val cacheCode = "USER.ID"
        },
        CC_USER_LOGIN {
            override val keyCode = CC_CODE
            override val entityType = ET_USER
            override val cacheCode = "USER.LOGIN"
        },
    }

    val isClosedUser: ClosedEntity = { it == ES_USER_CLOSED || it == ES_USER_BANNED }

    init {
        // allowed status changes
        ET_USER.registerAllowedStatusesChanges(
            mapOf(
                ES_USER_ACTUAL to setOf(ES_USER_BANNED, ES_USER_CLOSED),
                ES_USER_CLOSED to setOf(ES_USER_ACTUAL)
            )
        )
    }
}
