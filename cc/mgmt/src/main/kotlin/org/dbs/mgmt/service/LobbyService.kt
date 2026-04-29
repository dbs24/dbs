package org.dbs.mgmt.service

import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.lobby.LobbyCode
import org.dbs.lobby.LobbyCore.isClosedLobby
import org.dbs.mgmt.service.LobbyFactory.createHist
import org.dbs.mgmt.service.LobbyFactory.createNewLobby
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import org.dbs.mgmt.dao.LobbyDao as DAO
import org.dbs.mgmt.model.lobby.Lobby as ENTITY

@Service
class LobbyService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val lobbyDao: DAO,
) : AbstractApplicationService() {

    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
        if (entity.lobbyId != null) {
            r2dbcPersistenceService.saveEntityHist(createHist(entity)).awaitSingle()
            lobbyDao.invalidateCaches(entity.lobbyCode)
            entity
        } else this
    }

    suspend fun saveLobby(lobby: ENTITY): ENTITY = r2dbcPersistenceService.saveEntity(lobby).awaitSingle()

    suspend fun createNewLobby(lobbyLogin: LobbyCode): ENTITY {
        logger.debug { "create new lobby: $lobbyLogin" }
        return createNewLobby().copy(lobbyCode = lobbyLogin)
    }

    suspend fun findLobbyByLogin(lobbyLogin: LobbyCode): ENTITY? =
        lobbyDao.findLobbyByCode(lobbyLogin.also { logger.debug { "find lobby login: $lobbyLogin" } })

    fun setLobbyNewStatus(lobby: ENTITY, status: EntityStatusEnum): ENTITY =
        lobby.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedLobby(status)) now() else lobby.closeDate,
        )
}
