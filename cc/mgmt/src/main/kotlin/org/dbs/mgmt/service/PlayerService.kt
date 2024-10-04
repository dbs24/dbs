package org.dbs.mgmt.service

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.Email
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.r2dbcPersistenceService
import org.dbs.player.PlayerCore.PlayerActionEnum.EA_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.PlayerCore.isClosedPlayer
import org.dbs.player.PlayerLogin
import org.dbs.player.PlayerPassword
import org.dbs.sandbox.service.GrpcSandBoxClientService
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.service.v2.EntityCoreValExt.updateStatus
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.mgmt.dao.PlayerDao as DAO
import org.dbs.mgmt.model.player.Player as ENTITY

@Service
@Lazy(false)
class PlayerService(
    val grpcSandBoxClientService: GrpcSandBoxClientService,
    val dao: DAO,
    val passwordEncoder: PasswordEncoder,
    val playerFactory: PlayerFactory,
) : AbstractApplicationService() {
    //override fun initialize() = super.initialize().also { getOrCreateDefaultRootPlayer().subscribeMono() }

    override fun initialize() = super.initialize().also {

        runBlocking {
            getOrCreateDefaultRootPlayer().subscribeMono()
        }

        // 4 test only
        if (!env.junitMode)
            grpcSandBoxClientService.subscribe2Invites { item ->
                logger.debug { "receive answer: $item" }
            }
    }

    suspend fun getOrCreateDefaultRootPlayer(): Mono<ENTITY> =  // crete Root role if not exists
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

    //------------------------------------------------------------------------------------------------------------------
    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
            if (!justCreated.value)
                r2dbcPersistenceService.saveEntityHistCo(playerFactory.createHist(entity))
                    .let {
                        dao.invalidateCaches(entity.login)
                        entity
                    }
            else this
        }

    private val createRootPlayer: Mono<ENTITY> = runBlocking {
        generateNewEntityId().toMono()
            .map { playerFactory.createRootPlayer(it) }
            .flatMap { executeAction(it, EA_CREATE_OR_UPDATE_PLAYER) }
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
        player.run {
            dao.invalidateCaches(player.login)
            updateStatus(status, isClosedPlayer(status))
            this
        }

    fun setPlayerNewPassword(player: ENTITY, password: PlayerPassword): ENTITY =
        player.let {
            dao.invalidateCaches(it.login)
            it.copy(password = passwordEncoder.encode(password))
        }
}
