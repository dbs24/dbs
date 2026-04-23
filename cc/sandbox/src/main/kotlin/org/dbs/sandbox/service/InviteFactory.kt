package org.dbs.sandbox.service

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.MIN_DATE_TIME
import org.dbs.invite.InviteCore.EntityStatus.ES_INVITE_ACTUAL
import org.dbs.sandbox.model.hist.GameInviteHist
import org.dbs.sandbox.model.invite.GameInvite
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import kotlin.Int.Companion.MIN_VALUE
import org.dbs.sandbox.model.invite.GameInvite as ENTITY

@Service
class InviteFactory : AbstractApplicationService() {

    fun createNewInvite(inviteCode: EntityCode): ENTITY = ENTITY(
        playerLogin = EMPTY_STRING,
        inviteCode = inviteCode,
        gameType = MIN_VALUE,
        validDate = MIN_DATE_TIME,
        requiredRating = MIN_VALUE,
        whiteSide = false,
        entityStatus = ES_INVITE_ACTUAL,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun createHist(src: GameInvite): GameInviteHist = GameInviteHist(
        inviteId = src.inviteId!!,
        playerLogin = src.playerLogin,
        actualDate = src.modifyDate,
        inviteCode = src.inviteCode,
        gameType = src.gameType,
        validDate = src.validDate,
        requiredRating = src.requiredRating,
        whiteSide = src.whiteSide,
        entityStatus = src.entityStatus,
        createDate = src.createDate,
        modifyDate = src.modifyDate,
        closeDate = src.closeDate,
    )
}
