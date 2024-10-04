package org.dbs.auth.server.clients.players.client

import org.dbs.mgmt.service.GrpcMgmtClientService
import org.dbs.application.core.service.funcs.IntFuncs.errorS
import org.dbs.consts.EntityConsts.EntityStatuses.ACTUAL
import org.dbs.enums.I18NEnum.FLD_INVALID_PLAYER_STATUS
import org.dbs.grpc.ext.ResponseAnswerObj.unpackResponseEntity
import org.dbs.mgmt.client.PlayerCredentials
import org.dbs.player.PlayerLogin
import org.dbs.protobuf.core.MainResponse
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.importErrorsIfAny
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.validator.Error.PLAYER_INVALID_STATUS
import org.dbs.validator.Field.SSS_PLAYER_STATUS
import org.springframework.stereotype.Service

@Service
class PlayerClientService(private val grpcMgmtClientService: GrpcMgmtClientService) : AbstractApplicationService() {

    fun getAndValidatePlayerCredentials(
        playerLogin: PlayerLogin,
        answerBuilder: RAB,
    ): MainResponse = grpcMgmtClientService.getPlayerCredentials(playerLogin).also {

        answerBuilder.apply {
            if (importErrorsIfAny(it.responseAnswer)) {
                logger.warn { "$playerLogin: ${errorMessagesCount.errorS()}" }
            } else {
                // validate status
                logger.warn { "$playerLogin: ${errorMessagesCount.errorS()}" }

                with(it.responseAnswer.unpackResponseEntity<PlayerCredentials>()) {

                    require(playerLogin == this.playerLogin) { "invalid playerLogin - [$playerLogin<>${this.playerLogin}]" }

                    // allowed player statuses
                    if (playerStatus != ACTUAL) {
                        addErrorInfo(
                            RC_INVALID_RESPONSE_DATA,
                            PLAYER_INVALID_STATUS,
                            SSS_PLAYER_STATUS,
                            findI18nMessage(FLD_INVALID_PLAYER_STATUS, playerStatus),
                        )
                    }
                }
            }
        }
    }
}
