package org.dbs.mgmt.model.hist

import org.dbs.consts.OperDate
import org.dbs.lobby.*
import org.dbs.lobby.LobbyCore.EntityTypes.ET_LOBBY
import org.dbs.player.PlayerId
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("cc_lobbies_hist")
data class LobbyHist(
    @Id
    @Column("lobby_id")
    val lobbyId: LobbyId,
    @Column("actual_date")
    val actualDate: OperDate,
    @Column("lobby_kind")
    val lobbyKind: LobbyKind,
    @Column("owner_id")
    val ownerId: PlayerId,
    @Column("lobby_code")
    val lobbyCode: LobbyCode,
    @Column("lobby_name")
    val lobbyName: LobbyName,
    )  : EntityCoreVal(lobbyId, ET_LOBBY)
