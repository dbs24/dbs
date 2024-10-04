package org.dbs.sandbox.service

import org.dbs.consts.EntityCode
import org.dbs.consts.EntityId
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.MIN_DATE_TIME
import org.dbs.invite.InviteCore.EntityStatus.ES_INVITE_ACTUAL
import org.dbs.sandbox.model.hist.GameInviteHist
import org.dbs.sandbox.model.invite.GameInvite
import org.dbs.service.v2.EntityCoreValExt.asNew
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import kotlin.Int.Companion.MIN_VALUE
import org.dbs.sandbox.model.invite.GameInvite as ENTITY


@Service
class InviteFactory: AbstractApplicationService() {
    fun createNewInvite(id : EntityId, inviteCode: EntityCode) : ENTITY = ENTITY(
        inviteId = id,
        playerLogin = EMPTY_STRING,
        inviteCode = inviteCode,
        gameType = MIN_VALUE,
        validDate = MIN_DATE_TIME,
        requiredRating = MIN_VALUE,
        whiteSide = false
    ).asNew(ES_INVITE_ACTUAL)


    fun createHist(src: GameInvite) =
        GameInviteHist(
            inviteId = src.inviteId,
            playerLogin = src.playerLogin,
            actualDate = src.entityCore.modifyDate,
            inviteCode = src.inviteCode,
            gameType = src.gameType,
            validDate = src.validDate,
            requiredRating = src.requiredRating,
            whiteSide = src.whiteSide
        )
}
