package org.dbs.mgmt.service


import org.dbs.consts.EntityId
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.lobby.LobbyCore.EntityStatus.ES_LOBBY_ACTUAL
import org.dbs.mgmt.model.hist.LobbyHist
import org.dbs.mgmt.model.lobby.Lobby
import org.dbs.service.v2.EntityCoreValExt.asNew
import org.dbs.mgmt.model.lobby.Lobby as ENTITY

object LobbyFactory {
    fun LobbyService.createNewLobby(id: EntityId) : ENTITY =
        ENTITY(
            lobbyId = id,
            ownerId = 0,
            lobbyCode = id.hashCode().toString(),
            lobbyKind = 0,
            lobbyName = EMPTY_STRING,
        ).asNew(ES_LOBBY_ACTUAL)

    fun LobbyService.createHist(src: Lobby) =
        LobbyHist(
            actualDate = src.entityCore.modifyDate,
            lobbyId = src.lobbyId,
            ownerId = src.ownerId,
            lobbyCode = src.lobbyCode,
            lobbyKind = src.lobbyKind,
            lobbyName = src.lobbyName,
        )
}
