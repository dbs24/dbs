package org.dbs.mgmt.service

import org.dbs.lobby.LobbyCore.EntityStatus.ES_LOBBY_ACTUAL
import org.dbs.mgmt.model.hist.LobbyHist
import org.dbs.mgmt.model.lobby.Lobby
import java.time.LocalDateTime.now
import org.dbs.mgmt.model.lobby.Lobby as ENTITY

object LobbyFactory {

    fun LobbyService.createNewLobby(): ENTITY = ENTITY(
        lobbyId = null,
        ownerId = 0,
        lobbyCode = "",
        lobbyKind = 0,
        lobbyName = "",
        entityStatus = ES_LOBBY_ACTUAL,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun LobbyService.createHist(src: Lobby): LobbyHist = LobbyHist(
        actualDate = src.modifyDate,
        lobbyId = requireNotNull(src.lobbyId) { "Lobby must be persisted before creating history" },
        ownerId = src.ownerId,
        lobbyCode = src.lobbyCode,
        lobbyKind = src.lobbyKind,
        lobbyName = src.lobbyName,
        entityStatus = src.entityStatus,
        createDate = src.createDate,
        modifyDate = src.modifyDate,
        closeDate = src.closeDate,
    )
}
