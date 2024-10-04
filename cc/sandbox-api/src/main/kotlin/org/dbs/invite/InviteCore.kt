package org.dbs.invite

import org.dbs.consts.EntityConsts.EntityStatuses.ACCEPTED
import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.consts.EntityConsts.EntityStatuses.CANCELLED
import org.dbs.consts.EntityConsts.EntityStatuses.OVERDUE
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.consts.ClosedEntity
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_CODE
import org.dbs.entity.core.v2.consts.EntityV2Consts.CacheKeyId.CC_ID
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_CREATE_OR_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericAction.EGAS_UPDATE
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_ACCEPTED
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_ACTUAL
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_CANCELLED
import org.dbs.entity.core.v2.consts.EntityV2Consts.GenericStatuses.EGS_OVERDUE
import org.dbs.entity.core.v2.type.Application.SAND_BOX
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityTypeExtension.registerAllowedStatusesChanges
import org.dbs.invite.InviteConsts.Names.INVITE
import org.dbs.invite.InviteCore.EntityStatus.ES_INVITE_ACCEPTED
import org.dbs.invite.InviteCore.EntityStatus.ES_INVITE_ACTUAL
import org.dbs.invite.InviteCore.EntityStatus.ES_INVITE_CANCELLED
import org.dbs.invite.InviteCore.EntityStatus.ES_INVITE_OVERDUE
import org.dbs.invite.InviteCore.EntityTypes.ET_INVITE

object InviteCore : EntityCoreInitializer {

    // Types
    enum class EntityTypes : EntityTypeEnum {
        ET_INVITE {
            override val entityTypeId = 200100
            override val entityTypeName = INVITE
            override val module = SAND_BOX
        },
    }

    enum class EntityStatus : EntityStatusEnum {
        ES_INVITE_ACTUAL {
            override val statusCode = EGS_ACTUAL
            override val entityType = ET_INVITE
            override val entityStatusName = ACTUAL
        },
        ES_INVITE_ACCEPTED {
            override val statusCode = EGS_ACCEPTED
            override val entityType = ET_INVITE
            override val entityStatusName = ACCEPTED
        },
        ES_INVITE_CANCELLED {
            override val statusCode = EGS_CANCELLED
            override val entityType = ET_INVITE
            override val entityStatusName = CANCELLED
        },
        ES_INVITE_OVERDUE {
            override val statusCode = EGS_OVERDUE
            override val entityType = ET_INVITE
            override val entityStatusName = OVERDUE
        }
    }

    enum class InviteActionEnum : EntityActionEnum {
        EA_CREATE_OR_UPDATE_INVITE {
            override val actionCode = EGAS_CREATE_OR_UPDATE
            override val entityType = ET_INVITE
            override val actionName = "Create or update invite"
        },
        EA_UPDATE_INVITE_STATUS {
            override val actionCode = EGAS_UPDATE
            override val entityType = ET_INVITE
            override val actionName = "Update invite status"
        }
    }

    //
    // cache keys
    enum class CacheKeyInviteEnum : EntityCacheKeyEnum {
        CC_INVITE_ID {
            override val keyCode = CC_ID
            override val entityType = ET_INVITE
            override val cacheCode = "INVITE.ID"
        },
        CC_INVITE_CODE {
            override val keyCode = CC_CODE
            override val entityType = ET_INVITE
            override val cacheCode = "INVITE.CODE"
        },
    }

    val isClosedInvite: ClosedEntity = {
        it == ES_INVITE_CANCELLED || it == ES_INVITE_OVERDUE
    }

    init {
        // allowed status changes
        ET_INVITE.registerAllowedStatusesChanges(
            mapOf(
                ES_INVITE_ACTUAL to setOf(ES_INVITE_ACCEPTED, ES_INVITE_CANCELLED, ES_INVITE_OVERDUE),
            )
        )
    }
}
