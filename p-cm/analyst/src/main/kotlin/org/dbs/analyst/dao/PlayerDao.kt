package org.dbs.analyst.dao

import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.analyst.model.player.PlayerStatus
import org.dbs.analyst.model.player.Player
import org.dbs.analyst.repo.player.PlayerRepo
import org.dbs.analyst.repo.player.PlayerStatusRepo
import org.dbs.consts.Email
import org.dbs.player.enums.PlayerStatusEnum.Companion.isExistEnum
import org.dbs.player.enums.PlayerStatusEnum.entries
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import kotlin.system.measureTimeMillis

@Service
class PlayerDao internal constructor(
    val playerRepo: PlayerRepo,
    val playerStatusRepo: PlayerStatusRepo,
) : DaoAbstractApplicationService() {

    val statuses by lazy { entries.map { PlayerStatus(it, it.getValue()) } }
    fun synchronizeRefs() = measureTimeMillis {
        fromIterable(statuses)
            .publishOn(parallelScheduler)
            .noDuplicates({ it }, { it.playerStatusId }, { it.playerStatusName })
            .synchronizeReference(playerStatusRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })

        playerStatusRepo.findAll().validateDb { isExistEnum(it.id) }.count().subscribeMono()
    }.also { logger.debug { "synchronizePlayerStatus: took $it ms" } }

    fun findPlayerByLogin(login: String): Mono<Player> = playerRepo.findByLogin(login)
    fun findPlayerByEmail(email: Email): Mono<Player> = playerRepo.findByEmail(email)
    fun savePlayer(player: Player): Mono<Player> = playerRepo.save(player)

}
