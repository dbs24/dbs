package org.dbs.sandbox.service.grpc

import org.dbs.api.CommonJobs.JK_SAVE
import org.dbs.api.JobKey
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.INVITE_CODE
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_PLAYER_LOGIN
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.invite.InviteCore.InviteActionEnum.EA_CREATE_OR_UPDATE_INVITE
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.sandbox.mapper.InviteMappers.updateInviteFromDto
import org.dbs.sandbox.model.invite.GameInvite
import org.dbs.sandbox.service.ApplicationServiceGate.ServicesList.inviteService
import org.dbs.sandbox.service.grpc.GrpcCreateOrUpdateInvite.JobKeyImpl.JK_FIND_OR_CREATE_INVITE
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateOptionalField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.sandbox.invite.client.CreateOrUpdateInviteRequest as REQ
import org.dbs.sandbox.invite.client.CreatedInviteDto as ENT

object GrpcCreateOrUpdateInvite {

    private enum class JobKeyImpl : JobKey {
        JK_FIND_OR_CREATE_INVITE
    }

    suspend fun SandBoxGrpcService.createOrUpdateInviteInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findOrCreateInvite()
                    saveEntity()
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                val invite by lazy { LateInitVal<GameInvite>() }
                val invite4Update by lazy { LateInitVal<GameInvite>() }

                override fun isValidDto() = request.run {
                    with(rab) {
                        if (hasInviteCode()) {
                            validateOptionalField(inviteCode, INVITE_CODE, SSS_INVITE_CODE)
                        }
                        validateMandatoryField(playerLogin, LOGIN_PATTERN, SSS_PLAYER_LOGIN)
                        noErrors()
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                private fun updateFromDto(invite: GameInvite): GameInvite =
                    inviteService.updateInviteFromDto(invite, request)
                        .run { invite4Update.init(this) }

                //--------------------------------------------------------------------------------------------------------------
                private suspend fun saveInvite(invite: GameInvite): GameInvite =
                    executeAction(invite, EA_CREATE_OR_UPDATE_INVITE, remoteAddress, request.toString())

                //--------------------------------------------------------------------------------------------------------------
                suspend fun findOrCreateInvite() = launchJob(JK_FIND_OR_CREATE_INVITE) {
                    with(request) {
                        if (hasInviteCode()) {
                            inviteService.findInviteByCode(inviteCode)
                                ?.apply { invite.init(this) }

                            if (invite.isNotInitialized())
                                rab.addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    SSS_PLAYER_OLD_LOGIN,
                                    findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, inviteCode)
                                )
                        } else inviteService.createNewInviteCo().apply { invite.init(this) }
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = launchJob(JK_SAVE, setOf(JK_FIND_OR_CREATE_INVITE)) {
                    inviteService.saveHistory(invite.value)
                        .also { saveInvite(updateFromDto(invite.value)) }
                }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(JK_SAVE, ENT.newBuilder()) {
                    with(invite4Update.value) {
                        it.setInviteCode(inviteCode)
                            .setPlayerLogin(playerLogin)
                            .setStatus(invite4Update.value.status().entityStatusName)
                    }
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}