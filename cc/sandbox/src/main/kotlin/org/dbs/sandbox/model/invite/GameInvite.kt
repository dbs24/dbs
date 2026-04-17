package org.dbs.sandbox.model.invite

import org.dbs.consts.EntityCode
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.invite.InviteCore.EntityTypes.ET_INVITE
import org.dbs.invite.InviteId
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sb_game_invite")
data class GameInvite(
    @Id
    val inviteId: InviteId,

    val playerLogin: EntityCode,

    val inviteCode: EntityCode,

    val gameType: Int,

    val validDate: OperDate,

    val requiredRating: Int,

    val whiteSide: Boolean,

    @Column("status_id")
    val entityStatus: EntityStatusEnum,

    val createDate: OperDate,

    val modifyDate: OperDate,

    val closeDate: OperDateNull = null,

) : EntityCoreVal(inviteId, ET_INVITE) {

    @Transient
    override val entityType: EntityTypeEnum = ET_INVITE

    override fun status() = entityStatus
}
