package org.dbs.analyst.service

import org.dbs.analyst.dao.PlayerDao
import org.dbs.analyst.mapper.PlayerMappers
import org.dbs.analyst.model.player.Player
import org.dbs.application.core.service.funcs.TestFuncs.generateTestString20
import org.dbs.consts.Email
import org.dbs.consts.EntityStatusName
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.NOT_ASSIGNED
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.enums.EntityStatusEnum
import org.dbs.entity.core.enums.EntityStatusEnum.*
import org.dbs.entity.core.enums.EntityTypeEnum.SSS_ACTORS_MANAGER
import org.dbs.entity.core.enums.EntityTypeEnumExtension.registerAllowedStatusesChanges
import org.dbs.player.PlayerLogin
import org.dbs.player.enums.PlayerStatusEnum.PS_ACTUAL
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.defer
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
@Lazy(false)
class PlayerService(
    val playerDao: PlayerDao,
    val passwordEncoder: PasswordEncoder,
    val playerMappers: PlayerMappers,
) : AbstractRestApplicationService() {

    override fun initialize() = super.initialize().also {
        // experimental future: allowed statuses updated (from -> to)
//        SSS_ACTORS_MANAGER.registerAllowedStatusesChanges(
//            mapOf(     // statuses  "to" (listOf)
//                SSS_ACTORS_MANAGER_STATUS_ACTUAL to setOf(
//                    SSS_ACTORS_MANAGER_STATUS_BANNED,
//                    SSS_ACTORS_MANAGER_STATUS_CLOSED,
//                ),
//                SSS_ACTORS_MANAGER_STATUS_BANNED to setOf(
//                    SSS_ACTORS_MANAGER_STATUS_ACTUAL,
//                ),
//                SSS_ACTORS_MANAGER_STATUS_CLOSED to setOf(
//                    SSS_ACTORS_MANAGER_STATUS_ACTUAL,
//                )
//            )
//        )

        playerDao.synchronizeRefs()

        getOrCreateDefaultRootPlayer().subscribe()

    }

    fun getOrCreateDefaultRootPlayer(): Mono<Player> =  // crete Root role if not exists
        findPlayerByLogin(ROOT_USER)
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
    private val createRootPlayer: Mono<Player> = defer {
        generateNewEntityId(Player::class.java)
            .map { newEntityId ->
                Player(
                    playerId = newEntityId,
                    login = ROOT_USER,
                    firstName = ROOT_USER,
                    lastName = ROOT_USER,
                    email = ROOT_USER,
                    phone = NOT_ASSIGNED,
                    status = PS_ACTUAL,
                    password = passwordEncoder.encode(ROOT_USER_PASS),
                    token = generateTestString20()
                ).asNew<Player>()
            }.flatMap {
                playerDao.savePlayer(it)
            }
    }

    fun findPlayerByLogin(playerLogin: PlayerLogin): Mono<Player> =
        playerDao.findPlayerByLogin(playerLogin
            .also { logger.debug { "find player login: $playerLogin" } })

    fun findPlayerByEmail(playerEmail: Email): Mono<Player> =
        playerDao.findPlayerByEmail(playerEmail)

    fun createNewPlayer(playerLogin: PlayerLogin): Mono<Player> =
        generateNewEntityId(Player::class.java)
            .map { newEntityId ->
                logger.debug { "create new player login: $playerLogin (entityId=$newEntityId)" }
                Player(
                    playerId = newEntityId,
                    login = playerLogin,
                    email = EMPTY_STRING,
                    lastName = EMPTY_STRING,
                    firstName = EMPTY_STRING,
                    status = PS_ACTUAL,
                    password = EMPTY_STRING,
                    token = EMPTY_STRING,
                    phone = EMPTY_STRING,
                )
            }

    fun setPlayerNewStatus(player: Player, status: EntityStatusName): Mono<Player> =
        player.run {
            val entityStatusEnum = EntityStatusEnum.getEnum(SSS_ACTORS_MANAGER, status)
//            updateStatus(
//                entityStatusEnum, (entityStatusEnum == SSS_ACTORS_MANAGER_STATUS_CLOSED)
//                        || (entityStatusEnum == SSS_ACTORS_MANAGER_STATUS_BANNED)
//            )
            toMono()
        }

    fun savePlayer(player: Player): Mono<Player> = playerDao.savePlayer(player)


}
