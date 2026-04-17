package org.dbs.mgmt.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.Email
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.r2dbcPersistenceService
import org.dbs.player.PlayerCore.PlayerActionEnum.EA_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.PlayerCore.isClosedPlayer
import org.dbs.player.PlayerLogin
import org.dbs.player.PlayerPassword
import org.dbs.sandbox.service.GrpcSandBoxClientService
import org.dbs.service.v2.ActionEvent
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now
import org.dbs.mgmt.dao.PlayerDao as DAO
import org.dbs.mgmt.model.player.Player as ENTITY

@Service
@Lazy(false)
class PlayerService(
    val grpcSandBoxClientService: GrpcSandBoxClientService,
    val dao: DAO,
    val passwordEncoder: PasswordEncoder,
    val playerFactory: PlayerFactory,
    val objectMapper: ObjectMapper,
    val eventPublisher: ApplicationEventPublisher,
) : AbstractApplicationService() {

    override fun initialize() = super.initialize().also {
        runBlocking {
            getOrCreateDefaultRootPlayer().subscribeMono()
        }

        if (!env.junitMode)
            grpcSandBoxClientService.subscribe2Invites { item ->
                logger.debug { "receive answer: $item" }
            }
    }

    suspend fun getOrCreateDefaultRootPlayer(): Mono<ENTITY> =
        findPlayerByLogin(ROOT_USER).toMono()
            .flatMap { player ->
                player.run {
                    if (passwordEncoder.matches(ROOT_USER_PASS, password)) {
                        logger.warn("Please, replace default password for player '$ROOT_USER'".uppercase())
                    }
                    toMono()
                }
            }
            .switchIfEmpty { createRootPlayer }

    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
        if (!justCreated.value)
            r2dbcPersistenceService.saveEntityHistCo(playerFactory.createHist(entity))
                .let {
                    dao.invalidateCaches(entity.login)
                    entity
                }
        else this
    }

    suspend fun savePlayer(
        player: ENTITY,
        actionEnum: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): ENTITY = r2dbcPersistenceService.saveEntity(player).awaitSingle()
        .also {
            eventPublisher.publishEvent(
                ActionEvent(
                    entityId = it.playerId,
                    entityTypeId = it.entityType.entityTypeId,
                    actionCodeId = actionEnum.actionCodeId,
                    remoteAddr = remoteAddr,
                    actionNote = actionNote,
                )
            )
        }

    private val createRootPlayer: Mono<ENTITY> = runBlocking {
        generateNewEntityId().toMono()
            .map { playerFactory.createRootPlayer(it) }
            .flatMap { r2dbcPersistenceService.saveEntity(it) }
            .doOnSuccess { player ->
                eventPublisher.publishEvent(
                    ActionEvent(
                        entityId = player.playerId,
                        entityTypeId = player.entityType.entityTypeId,
                        actionCodeId = EA_CREATE_OR_UPDATE_PLAYER.actionCodeId,
                        remoteAddr = "system",
                        actionNote = "Root player created",
                    )
                )
            }
    }

    suspend fun createNewPlayer(playerLogin: PlayerLogin): ENTITY =
        generateNewEntityId()
            .let {
                logger.debug { "create new player login: $playerLogin (entityId=$it)" }
                playerFactory.createNewPlayer(it)
            }

    suspend fun findPlayerByLogin(playerLogin: PlayerLogin): ENTITY? =
        dao.findPlayerByLoginCo(playerLogin.also { logger.debug { "find player login: $playerLogin" } })

    suspend fun findPlayerByEmail(playerEmail: Email): ENTITY? =
        dao.findPlayerByEmailCo(playerEmail)

    fun setPlayerNewStatus(player: ENTITY, status: EntityStatusEnum): ENTITY =
        player.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedPlayer(status)) now() else player.closeDate,
        ).also {
            dao.invalidateCaches(player.login)
            it.justCreated.update(player.justCreated.value)
        }

    fun setPlayerNewPassword(player: ENTITY, password: PlayerPassword): ENTITY =
        player.copy(password = passwordEncoder.encode(password), modifyDate = now())
            .also {
                dao.invalidateCaches(player.login)
                it.justCreated.update(player.justCreated.value)
            }

    fun updatePlayer(src: ENTITY, actionNote: StringNote): ENTITY =
        src.copy(modifyDate = now())
            .also { it.justCreated.update(src.justCreated.value) }
}
