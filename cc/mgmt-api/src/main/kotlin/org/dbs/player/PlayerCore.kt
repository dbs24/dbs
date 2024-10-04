package org.dbs.player

import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.EntityConsts.EntityStatuses.ANONYMOUS
import org.dbs.consts.EntityConsts.EntityStatuses.BANNED
import org.dbs.consts.EntityConsts.EntityStatuses.CLOSED
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.ClosedEntity
import org.dbs.player.PlayerCore.EntityStatus.*
import org.dbs.player.PlayerCore.EntityTypes.ET_PLAYER
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_CODE
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_ID
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_CREATE_OR_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_UPDATE_PASSWORD
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_ACTUAL
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_ANONYMOUS
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_BANNED
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_CLOSED
import org.dbs.entity.core.v2.type.Application.CHESS
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityTypeExtension.registerAllowedStatusesChanges
import org.dbs.player.PlayersConsts.Names.PLAYER

object PlayerCore : EntityCoreInitializer {

    // Types
    enum class EntityTypes : EntityTypeEnum {
        ET_PLAYER {
            override val entityTypeId = 100100
            override val entityTypeName = PLAYER
            override val module = CHESS
        },
    }

    enum class EntityStatus : EntityStatusEnum {
        ES_PLAYER_ANONYMOUS {
            override val statusCode = EGS_ANONYMOUS
            override val entityType = ET_PLAYER
            override val entityStatusName = ANONYMOUS
        },
        ES_PLAYER_ACTUAL {
            override val statusCode = EGS_ACTUAL
            override val entityType = ET_PLAYER
            override val entityStatusName = ACTUAL
        },
        ES_PLAYER_CLOSED {
            override val statusCode = EGS_CLOSED
            override val entityType = ET_PLAYER
            override val entityStatusName = CLOSED
        },
        ES_PLAYER_BANNED {
            override val statusCode = EGS_BANNED
            override val entityType = ET_PLAYER
            override val entityStatusName = BANNED
        }
    }

    enum class PlayerActionEnum : EntityActionEnum {
        EA_CREATE_OR_UPDATE_PLAYER {
            override val actionCode = EGAS_CREATE_OR_UPDATE
            override val entityType = ET_PLAYER
            override val actionName = "Create or update player"
        },
        EA_UPDATE_PLAYER_STATUS {
            override val actionCode = EGAS_UPDATE
            override val entityType = ET_PLAYER
            override val actionName = "update player status"
        },
        EA_UPDATE_PLAYER_PASSWORD {
            override val actionCode = EGAS_UPDATE_PASSWORD
            override val entityType = ET_PLAYER
            override val actionName = "update player password"
        }
    }

    //
    // cache keys
    enum class CacheKeyPlayerEnum : EntityCacheKeyEnum {
        CC_PLAYER_ID {
            override val keyCode = CC_ID
            override val entityType = ET_PLAYER
            override val cacheCode = "PLAYER.ID"
        },
        CC_PLAYER_LOGIN {
            override val keyCode = CC_CODE
            override val entityType = ET_PLAYER
            override val cacheCode = "PLAYER.LOGIN"
        },
    }

    val isClosedPlayer: ClosedEntity = { it == ES_PLAYER_CLOSED || it == ES_PLAYER_BANNED }

    init {
        // allowed status changes
        ET_PLAYER.registerAllowedStatusesChanges(
            mapOf(
                ES_PLAYER_ANONYMOUS to setOf(ES_PLAYER_ACTUAL, ES_PLAYER_CLOSED, ES_PLAYER_BANNED),
                ES_PLAYER_ACTUAL to setOf(ES_PLAYER_BANNED, ES_PLAYER_CLOSED),
                ES_PLAYER_CLOSED to setOf(ES_PLAYER_ACTUAL)
            )
        )
    }
}
