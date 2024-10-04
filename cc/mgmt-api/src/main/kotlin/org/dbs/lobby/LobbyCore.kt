package org.dbs.lobby

import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.EntityConsts.EntityStatuses.CLOSED
import org.dbs.consts.EntityConsts.EntityStatuses.PLAY
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.ClosedEntity
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_CODE
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_ID
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_CREATE_OR_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_ACTUAL
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_CLOSED
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_PLAY
import org.dbs.entity.core.v2.type.Application.CHESS
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityTypeExtension.registerAllowedStatusesChanges
import org.dbs.lobby.LobbyConsts.Names.LOBBY
import org.dbs.lobby.LobbyCore.EntityStatus.*
import org.dbs.lobby.LobbyCore.EntityTypes.ET_LOBBY

object LobbyCore : EntityCoreInitializer {

    enum class EntityTypes : EntityTypeEnum {
        ET_LOBBY {
            override val entityTypeId = 100200
            override val entityTypeName = LOBBY
            override val module = CHESS
        }
    }

    enum class EntityStatus : EntityStatusEnum {
        ES_LOBBY_ACTUAL {
            override val statusCode = EGS_ACTUAL
            override val entityType = ET_LOBBY
            override val entityStatusName = ACTUAL
        },
        ES_LOBBY_PLAY {
            override val statusCode = EGS_PLAY
            override val entityType = ET_LOBBY
            override val entityStatusName = PLAY
        },
        ES_LOBBY_CLOSED {
            override val statusCode = EGS_CLOSED
            override val entityType = ET_LOBBY
            override val entityStatusName = CLOSED
        }
    }

    enum class LobbyActions : EntityActionEnum {
        EA_CREATE_OR_UPDATE_LOBBY {
            override val actionCode = EGAS_CREATE_OR_UPDATE
            override val entityType = ET_LOBBY
            override val actionName = "Create or update lobby"
        },
        EA_UPDATE_LOBBY_STATUS {
            override val actionCode = EGAS_UPDATE
            override val entityType = ET_LOBBY
            override val actionName = "Update lobby status"
        }
    }

    // cache keys
    enum class CacheKeyActionEnum : EntityCacheKeyEnum {
        CC_LOBBY_ID {
            override val keyCode = CC_ID
            override val entityType = ET_LOBBY
            override val cacheCode = "LOBBY.ID"
        },
        CC_LOBBY_CODE {
            override val keyCode = CC_CODE
            override val entityType = ET_LOBBY
            override val cacheCode = "LOBBY.CODE"
        },
    }

    val isClosedLobby: ClosedEntity = { it == ES_LOBBY_CLOSED }

    init {
        // allowed status changes
        ET_LOBBY.registerAllowedStatusesChanges(
            mapOf(
                ES_LOBBY_ACTUAL to setOf(ES_LOBBY_CLOSED, ES_LOBBY_PLAY),
                ES_LOBBY_PLAY to setOf(ES_LOBBY_ACTUAL),
                ES_LOBBY_CLOSED to setOf(ES_LOBBY_ACTUAL)
            )
        )
    }
}
