package org.dbs.mgmt.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.Email
import org.dbs.consts.StringNote
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.ext.SpringFuncs.registryEntityEvent
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.r2dbcPersistenceService
import org.dbs.player.PlayerCore.PlayerActionEnum.EA_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.PlayerCore.isClosedPlayer
import org.dbs.player.PlayerLogin
import org.dbs.player.PlayerPassword
import org.dbs.sandbox.service.GrpcSandBoxClientService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now
import org.dbs.mgmt.dao.PlayerDao as DAO
import org.dbs.mgmt.model.player.Player as ENTITY

@Service
@Lazy(false)
@DependsOn("r2dbcPersistenceService")
class PlayerService(
    val grpcSandBoxClientService: GrpcSandBoxClientService,
    val dao: DAO,
    val passwordEncoder: PasswordEncoder,
    val playerFactory: PlayerFactory,
) : AbstractApplicationService() {

    override fun initialize() = super.initialize().also {
        if (!env.junitMode)
            grpcSandBoxClientService.subscribe2Invites { item ->
                logger.debug { "receive answer: $item" }
            }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        mono { getOrCreateDefaultRootPlayer() }
            .flatMap { it }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                { logger.debug { "root player ready: ${it.login}" } },
                { logger.error("failed to seed root player", it) }
            )
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
            .switchIfEmpty { buildRootPlayerMono() }

    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
        if (entity.playerId != null)
            r2dbcPersistenceService.saveEntityHistCo(playerFactory.createHist(entity))
                .let {
                    dao.invalidateCaches(entity.login)
                    entity
                }
        else this
    }

    suspend fun savePlayer(player: ENTITY): ENTITY = r2dbcPersistenceService.saveEntity(player).awaitSingle()

    private fun buildRootPlayerMono(): Mono<ENTITY> =
        playerFactory.createRootPlayer().toMono()
            .flatMap { r2dbcPersistenceService.saveEntity(it) }
            .doOnSuccess { player ->
                eventPublisher.value.registryEntityEvent(
                    requireNotNull(player.playerId) { "playerId must be set after save" },
                    player.type.entityTypeId,
                    EA_CREATE_OR_UPDATE_PLAYER.actionCodeId,
                    "system",
                    "Root player created",
                )
            }

    suspend fun createNewPlayer(playerLogin: PlayerLogin): ENTITY =
        playerFactory.createNewPlayer(playerLogin).also {
            logger.debug { "create new player login: $playerLogin" }
        }

    suspend fun findPlayerByLogin(playerLogin: PlayerLogin): ENTITY? =
        dao.findPlayerByLoginCo(playerLogin.also { logger.debug { "find player login: $playerLogin" } })

    suspend fun findPlayerByEmail(playerEmail: Email): ENTITY? =
        dao.findPlayerByEmailCo(playerEmail)

    fun setPlayerNewStatus(player: ENTITY, status: EntityStatusEnum): ENTITY =
        player.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedPlayer(status)) now() else null,
        ).also {
            dao.invalidateCaches(player.login)
        }

    fun setPlayerNewPassword(player: ENTITY, password: PlayerPassword): ENTITY =
        player.copy(password = passwordEncoder.encode(password), modifyDate = now())
            .also {
                dao.invalidateCaches(player.login)
            }

    fun updatePlayer(src: ENTITY, actionNote: StringNote): ENTITY =
        src.copy(modifyDate = now())
}
