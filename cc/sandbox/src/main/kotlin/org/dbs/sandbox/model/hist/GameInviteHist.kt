package org.dbs.sandbox.model.hist

import org.dbs.consts.EntityCode
import org.dbs.consts.OperDate
import org.dbs.invite.InviteCore.EntityTypes.ET_INVITE
import org.dbs.invite.InviteId
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("sb_game_invite_hist")
data class GameInviteHist(
    @Id
    val inviteId: InviteId,
    val actualDate: OperDate,
    val playerLogin: EntityCode,
    val inviteCode: EntityCode,
    val gameType: Int,
    val validDate: OperDate,
    val requiredRating: Int?,
    val whiteSide: Boolean
)  : EntityCoreVal(inviteId, ET_INVITE)
