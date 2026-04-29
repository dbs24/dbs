package org.dbs.chess24.player

import api.TestConst.PLAYER_TEST_AMOUNT
import api.TestConst.REPEATED_KOTEST_AMOUNT
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.TestFuncs.generateTestPassword811
import org.dbs.application.core.service.funcs.TestFuncs.selectFrom
import org.dbs.chess24.funcs.AbstractChessTest
import org.dbs.chess24.funcs.PlayerFuncs.createOrUpdateTestPlayer
import org.dbs.chess24.funcs.PlayerFuncs.createTestPlayer
import org.dbs.chess24.funcs.PlayerFuncs.playerCount
import org.dbs.chess24.funcs.PlayerFuncs.playerStatusesNames
import org.dbs.chess24.funcs.PlayerFuncs.updatePlayerPassword
import org.dbs.chess24.funcs.PlayerFuncs.updatePlayerStatus
import org.dbs.mgmt.model.player.Player
import reactor.core.publisher.Flux.concat
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.boundedElastic


typealias MonoPlayer = Mono<Player>
class PlayerTests: AbstractChessTest({

    val scheduler = boundedElastic()
    val entitiesCount = REPEATED_KOTEST_AMOUNT

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val createPlayerCaption = "create players  ($entitiesCount)"
    Given(createPlayerCaption) {

        val players = concat(
            createCollection<MonoPlayer>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create player: $it" }
                    list.add(createTestPlayer())
                }
            })
            .collectList()
            .publishOn(scheduler)


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        When(createPlayerCaption) {
            Then(createPlayerCaption) {
                players
                    .flatMapMany { fromIterable(it) }
                    .flatMap { player ->
                        mono {
                            logger().debug { "try 2 create player: ${player.login}" }
                            createOrUpdateTestPlayer(
                                player.login,
                                player.email!!,
                                player.login,
                                player.email
                            )
                        }.flatMap { it }
                    }.count().awaitSingle()
            }

            // validate amount of players
            Then("validate amount of players ($entitiesCount + $PLAYER_TEST_AMOUNT)") {
                playerCount().shouldBe(entitiesCount + PLAYER_TEST_AMOUNT)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val updatePlayerCaption = "update players status ($entitiesCount)"
    Given(updatePlayerCaption) {

        val players = concat(
            createCollection<MonoPlayer>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create player 4 update status: $it" }
                    list.add(createTestPlayer())
                }
            })
            .collectList()
            .publishOn(scheduler)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val updatePlayerStatus = "update players status ($entitiesCount)"
        When(updatePlayerStatus) {
            Then(updatePlayerStatus) {
                players
                    .flatMapMany { fromIterable(it) }
                    .flatMap { player ->
                        mono {
                            logger().debug { "try 2 update player status: ${player.login}" }
                            updatePlayerStatus(
                                player.login,
                                selectFrom(playerStatusesNames)
                            )
                        }.flatMap { it }
                    }.count().awaitSingle()
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val updatePlayerPassword = "update players password ($entitiesCount)" // why do we need entitiesCount here
        When(updatePlayerPassword) {
            Then(updatePlayerPassword) {
                players
                    .flatMapMany { fromIterable(it) }
                    .flatMap { player ->
                        mono {
                            logger().debug { "try 2 update player password: ${player.login}" }
                            updatePlayerPassword(
                                player.login,
                                generateTestPassword811(),
                            )
                        }.flatMap { it }
                    }.count().awaitSingle()
            }
        }
    }
})
