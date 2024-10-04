package org.dbs.sandbox.mapper

import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.sandbox.invite.client.CreateOrUpdateInviteRequest
import org.dbs.sandbox.model.invite.GameInvite
import org.dbs.sandbox.service.InviteService
import org.dbs.service.v2.EntityCoreValExt.copyEntity

typealias Dto2Player = (src: GameInvite, srcDto: CreateOrUpdateInviteRequest) -> GameInvite

object InviteMappers {

    val dto2Invite: Dto2Player = { src, dto ->
        src.copyEntity {
            src.copy(
                playerLogin = dto.playerLogin,
                gameType = dto.gameType,
                validDate = dto.validDate.toLocalDateTime(),
                requiredRating = dto.requiredRating,
                whiteSide = dto.whiteSide
            )
        }
    }

    inline fun InviteService.updateInviteFromDto(src: GameInvite, srcDto: CreateOrUpdateInviteRequest): GameInvite = run {
        dto2Invite(src, srcDto)
    }

}
