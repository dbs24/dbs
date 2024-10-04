package org.dbs.chess24.sandbox

import api.TestConst.REPEATED_KOTEST_AMOUNT
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.TestFuncs.selectFrom
import org.dbs.chess24.funcs.AbstractSandBoxTest
import org.dbs.chess24.funcs.InviteFuncs.createOrUpdateTestInvite
import org.dbs.chess24.funcs.InviteFuncs.createTestInvite
import org.dbs.chess24.funcs.InviteFuncs.inviteCount
import org.dbs.chess24.funcs.InviteFuncs.inviteStatusesNames
import org.dbs.chess24.funcs.InviteFuncs.updateInviteStatus
import org.dbs.sandbox.model.invite.GameInvite
import reactor.core.publisher.Flux.concat
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.boundedElastic

typealias MonoInvite = Mono<GameInvite>

class InviteTests() : AbstractSandBoxTest({

    val scheduler = boundedElastic()
    val entitiesCount = REPEATED_KOTEST_AMOUNT

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val createInviteCaption = "create invites  ($entitiesCount)"
    Given(createInviteCaption) {

        val invites = concat(
            createCollection<MonoInvite>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create invite: $it" }
                    list.add(createTestInvite())
                }
            })
            .collectList()
            .publishOn(scheduler)


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val updateInviteCaption = "update invites  ($entitiesCount)"
        When(updateInviteCaption) {
            Then(updateInviteCaption) {
                invites
                    .flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            logger().debug { "try 2 create invite: ${it.inviteCode}" }
                            createOrUpdateTestInvite(it.inviteCode)
                        }
                    }.count().awaitSingle()
            }

            // validate amount of invites
            Then("validate amount of created invites ($entitiesCount)") {
                inviteCount().shouldBe(entitiesCount)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val updateInviteCaption = "update invites status ($entitiesCount)"
    Given(updateInviteCaption) {

        val invites = concat(
            createCollection<MonoInvite>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create invite 4 update status: $it" }
                    list.add(createTestInvite())
                }
            })
            .collectList()
            .publishOn(scheduler)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val updateInviteStatus = "update invites status ($entitiesCount)"
        When(updateInviteStatus) {
            Then(updateInviteStatus) {
                invites
                    .flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            logger().debug { "try 2 update invite status: ${it.inviteCode}" }
                            updateInviteStatus(
                                it.inviteCode,
                                selectFrom(inviteStatusesNames)
                            )
                        }
                    }.count().awaitSingle()
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val grpcStreaming = "grpc stream subscribe 2 invite"
    Given(grpcStreaming) {

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val grpcStreaming = "subscribe 2 invite ($entitiesCount)"
        When(grpcStreaming) {
            Then(grpcStreaming) {
                grpcSandBoxClientService.subscribe2Invites { item ->
                    //println ( "test: receive answer: $item" )
                    println ( "receive answer: ${item.hashCode()}" )
                }
            }
        }
    }
})
