package com.uonmap.analyst.player.h2

import com.uonmap.actors.AbstractPlayersCoreTest
import io.kotest.common.ExperimentalKotest
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.api.LateInitVal
import org.dbs.cm.client.PlayerCredentialsRequest
import org.dbs.cm.client.PlayerCredentialsResponse

@OptIn(ExperimentalKotest::class)
class PlayerTestsH2 : AbstractPlayersCoreTest({

    //==================================================================================================================
    val createPlayersCaption = "get players credentials ($entitiesCount)"
    Given(createPlayersCaption) {

        val players = "TestLogin"

        When(createPlayersCaption) {

            val mcResponse by lazy { LateInitVal <PlayerCredentialsResponse>() }

            Then(createPlayersCaption) {
                mcResponse.hold(
                    grpcStub.getPlayerCredentials(
                        PlayerCredentialsRequest.newBuilder().setPlayerLogin(players).build()
                    )
                )
            }

            // validate amount of player passwords
            Then("validate players credentials ($entitiesCount)") {
                //playerPasswordsCount().subscribeMono().shouldBe(entitiesCount)
                logger.info { " validate mc " }
            }
        }
    }
})
