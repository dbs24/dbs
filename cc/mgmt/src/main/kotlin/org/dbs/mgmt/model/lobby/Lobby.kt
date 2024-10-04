package org.dbs.mgmt.model.lobby

import org.dbs.lobby.LobbyCode
import org.dbs.lobby.LobbyCore.EntityTypes.ET_LOBBY
import org.dbs.lobby.LobbyId
import org.dbs.lobby.LobbyKind
import org.dbs.lobby.LobbyName
import org.dbs.player.PlayerId
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("cc_lobbies")
data class Lobby(
    @Id
    @Column("lobby_id")
    val lobbyId: LobbyId,
    @Column("lobby_kind")
    val lobbyKind: LobbyKind,
    @Column("owner_id")
    val ownerId: PlayerId,
    @Column("lobby_code")
    val lobbyCode: LobbyCode,
    @Column("lobby_name")
    val lobbyName: LobbyName,
) : EntityCoreVal(lobbyId, ET_LOBBY)
