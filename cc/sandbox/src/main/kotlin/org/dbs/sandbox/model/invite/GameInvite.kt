package org.dbs.sandbox.model.invite

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.invite.InviteCore.EntityTypes.ET_INVITE
import org.dbs.invite.InviteId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sb_game_invite")
data class GameInvite(
    @Id
    val inviteId: InviteId? = null,

    val playerLogin: EntityCode,

    val inviteCode: EntityCode,

    val gameType: Int,

    val validDate: OperDate,

    val requiredRating: Int,

    val whiteSide: Boolean,

    @Column("status_id")
    val entityStatus: EntityStatusEnum,

    override val createDate: OperDate,

    override val modifyDate: OperDate,

    override val closeDate: OperDateNull = null,

    ) : EntityCore {

    @get:JsonIgnore
    override val entityId: EntityId? get() = inviteId

    override fun entityType() = ET_INVITE

    override fun status() = entityStatus
}