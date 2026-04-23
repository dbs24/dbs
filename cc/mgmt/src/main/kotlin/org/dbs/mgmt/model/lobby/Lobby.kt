package org.dbs.mgmt.model.lobby

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.lobby.LobbyCode
import org.dbs.lobby.LobbyCore.EntityTypes.ET_LOBBY
import org.dbs.lobby.LobbyId
import org.dbs.lobby.LobbyKind
import org.dbs.lobby.LobbyName
import org.dbs.player.PlayerId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@Table("cc_lobbies")
data class Lobby(
    @Id
    @Column("lobby_id")
    val lobbyId: LobbyId? = null,

    @Column("lobby_kind")
    val lobbyKind: LobbyKind,

    @Column("owner_id")
    val ownerId: PlayerId,

    @Column("lobby_code")
    val lobbyCode: LobbyCode,

    @Column("lobby_name")
    val lobbyName: LobbyName,

    @Column("status_id")
    val entityStatus: EntityStatusEnum,

    val createDate: OperDate,

    val modifyDate: OperDate,

    val closeDate: OperDateNull = null,

) : EntityCore {

    @get:JsonIgnore
    override val entityId: EntityId? get() = lobbyId

    @get:JsonIgnore
    val entityType: EntityTypeEnum get() = ET_LOBBY

    override fun status() = entityStatus
}
