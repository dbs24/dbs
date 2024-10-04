package org.dbs.analyst.service.grpc

import org.dbs.analyst.model.player.Player
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.entity.core.enums.EntityStatusEnum
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_PLAYER_LOGIN
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmInTransaction
import org.dbs.ext.GrpcFuncs.fmRab
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.player.PlayerLogin
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.GrpcResponse
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.SSS_PLAYER_LOGIN
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.cm.client.CreatedPlayerStatus as ENT
import org.dbs.cm.client.UpdatePlayerStatusRequest as REQ
import org.dbs.cm.client.UpdatePlayerStatusResponse as RESP

object GrpcUpdatePlayerStatus {
    suspend fun CmGrpcService.updatePlayerStatusInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get()
    ): RESP = request.run dto@{

        validateRemoteAddress(remoteAddress)

        buildGrpcResponse({
            it.run {

                val entityBuilder by lazy { ENT.newBuilder() }
                val newPlayerStatusEnum by lazy { LateInitVal<EntityStatusEnum>() }
                val playerLogin by lazy { LateInitVal<PlayerLogin>() }
                val modifiedPlayer by lazy { LateInitVal<Player>() }

                fun validateRequestData(): Boolean = run {
                    validateMandatoryField(
                        modifiedLogin,
                        LOGIN_PATTERN,
                        SSS_PLAYER_LOGIN
                    ) { playerLogin.hold(it.trim()) }
//                    validateListItem(
//                        newStatus,
//                        PlayerService.allowedShortPlayerStatuses,
//                        SSS_PLAYER_STATUS
//                    ) { shortStatusName ->
//                        newPlayerStatusEnum.hold(PlayerService.allowedPlayerStatuses
//                            .first { it.entityStatusShortName == shortStatusName })
//                    }
                    noErrors()
                }

                fun findModifiedPlayer(): MonoRAB = fmStart {
                    playerService.findPlayerByLogin(modifiedLogin)
                        .map { modifiedPlayer.hold(it); this }
                        .switchIfEmpty {
                            addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, playerLogin)
                            )
                            empty()
                        }
                }

                fun MonoRAB.validateNewPlayerStatus() = fmRab {
//                    validateEntityUpdateStatus(
//                        modifiedPlayer.value.status.getStatusCode(),
//                        newPlayerStatusEnum.value,
//                        SSS_PLAYER_STATUS
//                    )
                    it.toMono()
                }

                fun MonoRAB.save(): MonoRAB = fmInTransaction {
                        playerService.setPlayerNewStatus(
                            modifiedPlayer.value,
                            newPlayerStatusEnum.value.entityStatusShortName
                        )
                            .flatMap {
                                playerService.savePlayer(modifiedPlayer.value)
                    }
                }

                fun MonoRAB.finishResponseEntity() = fmFinish {
                        entityBuilder
                            .setModifiedLogin(modifiedPlayer.value.login)
                            .setNewStatus(modifiedPlayer.value.status.getStatusCode())
                }

                if (validateRequestData()) {
                    processGrpcResponse {
                        findModifiedPlayer()
                            .validateNewPlayerStatus()
                            .save()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        })
        { grpcResponse(it) }
    }
}
