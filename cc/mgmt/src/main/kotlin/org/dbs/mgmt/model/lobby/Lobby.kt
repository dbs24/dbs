package org.dbs.mgmt.model.lobby

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
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

    override val createDate: OperDate,
    override val modifyDate: OperDate,
    override val closeDate: OperDateNull = null,

) : EntityCore {

    @get:JsonIgnore
    override val entityId: EntityId? get() = lobbyId
    @get:JsonIgnore
    override val type get() = ET_LOBBY
    @get:JsonIgnore
    override val status get() = entityStatus
}
