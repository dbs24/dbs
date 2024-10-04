package org.dbs.mgmt.repo.lobby

import org.dbs.consts.EntityCode
import org.dbs.lobby.LobbyId
import org.dbs.mgmt.model.lobby.Lobby
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface LobbyRepo : CoroutineCrudRepository<Lobby, LobbyId> {
    suspend fun findByLobbyCode(login: EntityCode): Lobby?
}
