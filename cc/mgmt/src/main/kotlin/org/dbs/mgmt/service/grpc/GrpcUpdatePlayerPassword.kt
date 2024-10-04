package org.dbs.mgmt.service.grpc

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_PLAYER_LOGIN
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.mgmt.model.player.Player
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.playerService
import org.dbs.player.PlayerCore.PlayerActionEnum.EA_UPDATE_PLAYER_PASSWORD
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.mgmt.client.CreatedPlayerPassword as ENT
import org.dbs.mgmt.client.UpdatePlayerPasswordRequest as REQ

object GrpcUpdatePlayerPassword {

    suspend fun MgmtGrpcService.updatePlayerPasswordInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findModifiedPlayer()
                    saveEntity()
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                private val player by lazy { LateInitVal<Player>("player") }

                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(
                            modifiedLogin,
                            LOGIN_PATTERN,
                            SSS_PLAYER_LOGIN
                        )
                        validateMandatoryField(
                            newPassword,
                            PASSWORD_PATTERN,
                            SSS_PLAYER_PASSWORD
                        )
                        noErrors()
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun findModifiedPlayer() {
                    with(request) {
                        playerService.findPlayerByLogin(modifiedLogin)
                            ?.apply {
                                player.init(this)
                            }

                        if (player.isNotInitialized()) let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_OLD_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, modifiedLogin)
                            )
                        }
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = playerService.takeIf { player.isInitialized() }
                    ?.apply {
                        setPlayerNewPassword(
                            player.value,
                            request.newPassword
                        )
                        executeAction(player.value, EA_UPDATE_PLAYER_PASSWORD, remoteAddress, request.toString())
                    }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setModifiedLogin(player.value.login)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
