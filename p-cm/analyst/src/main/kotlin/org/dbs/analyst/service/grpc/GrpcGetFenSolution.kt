package org.dbs.analyst.service.grpc

import org.dbs.analyst.model.player.Player
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.FEN_PATTER
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.kafka.consts.KafkaTopicEnum.CM_SEND_TASK
import org.dbs.player.kafka.UciTask
import org.dbs.service.GrpcResponse
import org.dbs.service.MonoRAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.foundStdEntityServiceMsg
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateMinMax
import org.dbs.validator.Error.FEN_INVALID_DEPTH_VALUE
import org.dbs.validator.Error.FEN_INVALID_TIMEOUT_VALUE
import org.dbs.validator.Field.*
import reactor.kotlin.core.publisher.toMono
import org.dbs.cm.client.CreatedSolution as ENT
import org.dbs.cm.client.GetSolutionRequest as REQ
import org.dbs.cm.client.GetSolutionResponse as RESP


object GrpcGetFenSolution {
    suspend fun CmGrpcService.getFunSolutionInternal(
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
                        fen,
                        FEN_PATTER,
                        SSS_FEN
                    ) {
                        // found player
                        logger.debug("receive fen: '$fen'")
                        //entityBuilder.run { setPlayerLogin(playerLogin) }
                    }
                    validateMinMax(
                        depth,
                        3,
                        100,
                        FEN_INVALID_DEPTH_VALUE,
                        SSS_FEN_DEPTH
                    )
                    validateMinMax(
                        timeout,
                        1000,
                        3600000,
                        FEN_INVALID_TIMEOUT_VALUE,
                        SSS_FEN_TIMEOUT
                    )
                    noErrors()
                }

                //======================================================================================================
                fun findFen(): MonoRAB = fmStart {
//                    playerService.findPlayerByLogin("playerLogin")
//                        .flatMap { player.hold(it); toMono() }
//                        .switchIfEmpty {
//                            addErrorInfo(
//                                RC_INVALID_RESPONSE_DATA,
//                                INVALID_ENTITY_ATTR,
//                                SSS_PLAYER_LOGIN,
//                                "${findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN)} (playerLogin)"
//                            )
//                            empty()
//                        }
                    it.toMono()
                }

                //======================================================================================================
                fun MonoRAB.finishResponseEntity() = fmFinish {
                    it.also {
//                        player.value.run {
//                            entityBuilder.setPlayerLogin(playerLogin)
//                                .setPlayerPassword(password)
//                                .setPlayerStatus(status.getStatusCode())
                        foundStdEntityServiceMsg(Player::class.java, "playerLogin")
//                        }
                    }
                }
                //======================================================================================================
                // validate requestData
                if (validateRequestData()) {
                    // process endpoint
                    kafkaUniversalService.send(CM_SEND_TASK, UciTask(fen, 3, depth, timeout.toLong()))
                    processGrpcResponse {
                        findFen()
//                            .findPlayerPassword()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }) { grpcResponse(it) }
    }
}
