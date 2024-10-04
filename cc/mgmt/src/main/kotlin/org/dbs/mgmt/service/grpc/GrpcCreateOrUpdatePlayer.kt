package org.dbs.mgmt.service.grpc

import org.dbs.api.CommonJobs.JK_SAVE
import org.dbs.api.JobKey
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_FIRST_NAME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_LAST_NAME_PATTERN
import org.dbs.application.core.service.funcs.StringFuncs.isNull
import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.enums.I18NEnum.*
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.mgmt.mapper.PlayerMappers.updatePlayer
import org.dbs.mgmt.model.player.Player
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.playerService
import org.dbs.mgmt.service.grpc.GrpcCreateOrUpdatePlayer.JobKeyImp.JK_FIND_OR_CREATE_PLAYER
import org.dbs.player.PlayerCore.PlayerActionEnum.EA_CREATE_OR_UPDATE_PLAYER
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateOptionalEmail
import org.dbs.service.validator.GrpcValidators.validateOptionalField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.mgmt.client.CreateOrUpdatePlayerRequest as REQ
import org.dbs.mgmt.client.CreatedPlayerDto as ENT

object GrpcCreateOrUpdatePlayer {

    private enum class JobKeyImp : JobKey {
        JK_FIND_OR_CREATE_PLAYER,
    }

    suspend fun MgmtGrpcService.createOrUpdatePlayerInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    validateNewLogin()
                    findOrCreatePlayer()
                    saveEntity()
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                private val player by lazy { LateInitVal<Player>("player") }
                private val player4update by lazy { LateInitVal<Player>("player4update") }
                private val oldPlayerLogin by lazy { request.oldLogin.grpcGetOrNull() }

                //======================================================================================================
                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(login, LOGIN_PATTERN, SSS_PLAYER_LOGIN)
                        validateOptionalField(oldLogin, LOGIN_PATTERN, SSS_PLAYER_LOGIN)
                        validateOptionalField(firstName, USER_FIRST_NAME_PATTERN, SSS_PLAYER_FIRST_NAME)
                        validateOptionalField(middleName, USER_LAST_NAME_PATTERN, SSS_PLAYER_LAST_NAME)
                        validateOptionalField(lastName, USER_LAST_NAME_PATTERN, SSS_PLAYER_MIDDLE_NAME)
                        validateOptionalEmail(email, SSS_PLAYER_EMAIL)
                        // is update player
                        oldPlayerLogin?.let {
                            validateMandatoryField(it, LOGIN_PATTERN, SSS_PLAYER_OLD_LOGIN)
                        } ?: password?.let { playerPwd ->
                            validateMandatoryField(playerPwd, PASSWORD_PATTERN, SSS_PLAYER_PASSWORD)
                        } ?: addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            SSS_PLAYER_PASSWORD,
                            findI18nMessage(FLD_INVALID_PLAYER_PASSWORD)
                        )
                        noErrors()
                    }
                }

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewLogin(newLogin: EntityCode) =
                    playerService.findPlayerByLogin(newLogin)
                        ?.apply {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_LOGIN,
                                "${findI18nMessage(EXIST_PLAYER_LOGIN)} '${newLogin}'"
                            )
                        }

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewEmail(newEmail: Email) =
                    playerService.findPlayerByEmail(newEmail)
                        ?.apply {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_EMAIL,
                                "${findI18nMessage(EXIST_PLAYER_EMAIL)} '${newEmail}'"
                            )
                        }

                //------------------------------------------------------------------------------------------------------
                private fun updateFromDto(player: Player): Player =
                    playerService.updatePlayer(player, request)
                        .run { player4update.init(this) }

                //------------------------------------------------------------------------------------------------------
                private suspend fun savePlayer(player: Player): Player =
                    executeAction(player, EA_CREATE_OR_UPDATE_PLAYER, remoteAddress, request.toString())

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewLogin() =
                    request.apply {
                        val checkNewLogin = oldPlayerLogin?.let { it != login } ?: true
                        val checkNewEmail = oldPlayerLogin.isNull() || oldEmail?.let { it != email } ?: true
                        if (checkNewLogin) validateNewLogin(login)
                        if (checkNewEmail) validateNewEmail(email)
                    }

                //------------------------------------------------------------------------------------------------------
                suspend fun findOrCreatePlayer() = launchJob(JK_FIND_OR_CREATE_PLAYER) {
                    playerService.findPlayerByLogin(oldPlayerLogin ?: request.login)
                        ?.apply { player.init(this) }
                        ?: oldPlayerLogin?.let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_OLD_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, it)
                            )
                        } ?: playerService.createNewPlayer(request.login).apply { player.init(this) }
                }

                //------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = launchJob(JK_SAVE, JK_FIND_OR_CREATE_PLAYER) {
                    playerService.saveHistory(player.value)
                        .also { savePlayer(updateFromDto(player.value)) }
                }

                //------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(JK_SAVE, ENT.newBuilder()) {
                    it.setPlayerLogin(player4update.value.login)
                        .setEmail(player4update.value.email)
                        .setStatus(player4update.value.status().entityStatusName)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
