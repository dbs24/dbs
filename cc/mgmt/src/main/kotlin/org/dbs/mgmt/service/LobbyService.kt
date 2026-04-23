package org.dbs.mgmt.service

import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.lobby.LobbyCode
import org.dbs.lobby.LobbyCore.isClosedLobby
import org.dbs.mgmt.service.LobbyFactory.createHist
import org.dbs.mgmt.service.LobbyFactory.createNewLobby
import org.dbs.service.Extensions.registryEvent
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now
import org.dbs.mgmt.dao.LobbyDao as DAO
import org.dbs.mgmt.model.lobby.Lobby as ENTITY

@Service
class LobbyService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val lobbyDao: DAO,
    val eventPublisher: ApplicationEventPublisher,
) : AbstractApplicationService() {

    fun saveHistory(entity: ENTITY): reactor.core.publisher.Mono<ENTITY> =
        entity.run {
            if (entity.lobbyId != null)
                r2dbcPersistenceService.saveEntityHist(createHist(entity))
                    .map {
                        lobbyDao.invalidateCaches(entity.lobbyCode)
                        this
                    }
            else toMono()
        }

    suspend fun saveLobby(
        lobby: ENTITY,
        actionEnum: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): ENTITY = r2dbcPersistenceService.saveEntity(lobby).awaitSingle()
        .also {
            eventPublisher.registryEvent(
                requireNotNull(it.lobbyId) { "lobbyId must be set after save" },
                it.entityType.entityTypeId,
                actionEnum.actionCodeId,
                remoteAddr,
                actionNote,
            )
        }

    suspend fun createNewLobby(lobbyLogin: LobbyCode): reactor.core.publisher.Mono<ENTITY> {
        logger.debug { "create new lobby: $lobbyLogin" }
        return createNewLobby().toMono()
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
