package org.dbs.mgmt.service

import org.dbs.entity.core.EntityStatusEnum
import org.dbs.lobby.LobbyCode
import org.dbs.lobby.LobbyCore.isClosedLobby
import org.dbs.mgmt.service.LobbyFactory.createHist
import org.dbs.mgmt.service.LobbyFactory.createNewLobby
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.service.v2.EntityCoreValExt.updateStatus
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import org.dbs.mgmt.dao.LobbyDao as DAO
import org.dbs.mgmt.model.lobby.Lobby as ENTITY

@Service
class LobbyService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val lobbyDao: DAO,
) : AbstractApplicationService() {

    //------------------------------------------------------------------------------------------------------------------
    fun saveHistory(entity: ENTITY): Mono<ENTITY> =
        entity.run {
            if (!justCreated.value)
                r2dbcPersistenceService.saveEntityHist(createHist(entity))
                    .map {
                        lobbyDao.invalidateCaches(entity.lobbyCode)
                        this
                    }
            else toMono()
        }

    suspend fun createNewLobby(lobbyLogin: LobbyCode): Mono<ENTITY> =
        generateNewEntityId().toMono()
            .map { newEntityId ->
                logger.debug { "create new lobby login: $lobbyLogin (entityId=$newEntityId)" }
                createNewLobby(newEntityId)
            }

    suspend fun findLobbyByLogin(lobbyLogin: LobbyCode): ENTITY? =
        lobbyDao.findLobbyByCode(lobbyLogin.also { logger.debug { "find lobby login: $lobbyLogin" } })
    
    fun setLobbyNewStatus(lobby: ENTITY, status: EntityStatusEnum): Mono<ENTITY> =
        lobby.run {
            updateStatus(status, isClosedLobby(status))
            toMono()
        }
}
