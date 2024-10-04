package org.dbs.analyst.service.grpc

import org.dbs.analyst.model.player.Player
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.STR4M
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_PLAYER_LOGIN
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.GrpcResponse
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.foundStdEntityServiceMsg
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.SSS_PLAYER_LOGIN
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.cm.client.PlayerCredentials as ENT
import org.dbs.cm.client.PlayerCredentialsRequest as REQ
import org.dbs.cm.client.PlayerCredentialsResponse as RESP


object GrpcGetPlayerCredentials {
    suspend fun CmGrpcService.getPlayerCredentialsInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        val player by lazy { LateInitVal<Player>() }

        buildGrpcResponse({
            it.run {

                val entityBuilder by lazy { ENT.newBuilder() }

                //======================================================================================================
                fun validateRequestData(): Boolean = run {
                    validateMandatoryField(
                        playerLogin,
                        LOGIN_PATTERN.takeUnless { playerLogin == ROOT_USER } ?: STR4M,
                        SSS_PLAYER_LOGIN
                    ) {
                        // found player
                        logger.debug("receive player login: '$playerLogin'")
                        entityBuilder.run { setPlayerLogin(playerLogin) }
                    }
                    noErrors()
                }

                //======================================================================================================
                fun findPlayer(): MonoRAB = fmStart {
                    playerService.findPlayerByLogin(playerLogin)
                        .flatMap { player.hold(it); toMono() }
                        .switchIfEmpty {
                            addErrorInfo(
                                RC_INVALID_RESPONSE_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, playerLogin)
                            )
                            empty()
                        }
                }

                //======================================================================================================
                fun MonoRAB.finishResponseEntity() = fmFinish {
                    it.also {
                        player.value.run {
                            entityBuilder.setPlayerLogin(playerLogin)
                                .setPlayerPassword(password)
                                .setPlayerStatus(status.getStatusCode())
                            foundStdEntityServiceMsg(Player::class.java, playerLogin)
                        }
                    }
                }
                //======================================================================================================
                // validate requestData
                if (validateRequestData()) {
                    // process endpoint
                    processGrpcResponse {
                        findPlayer()
//                            .findPlayerPassword()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }) { grpcResponse(it) }
    }
}
