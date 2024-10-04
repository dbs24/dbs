package org.dbs.sandbox.service.grpc

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.INVITE_CODE
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_INVITE_CODE
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.invite.InviteCore.InviteActionEnum.EA_UPDATE_INVITE_STATUS
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.sandbox.model.invite.GameInvite
import org.dbs.sandbox.service.ApplicationServiceGate.ServicesList.inviteService
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.findEntityStatus
import org.dbs.service.validator.GrpcValidators.validateEntityUpdateStatus
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import org.dbs.protobuf.core.MainResponse as RESP
import org.dbs.sandbox.invite.client.CreatedInviteStatus as ENT
import org.dbs.sandbox.invite.client.UpdateInviteStatusRequest as REQ

object GrpcUpdateInviteStatus {
    suspend fun SandBoxGrpcService.updateInviteStatusInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findModifiedInvite()
                    saveEntity()
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                private val invite by lazy { LateInitVal<GameInvite>("gameInvite") }
                val newStatus4update by lazy { LateInitVal<EntityStatusEnum>() }

                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(inviteCode, INVITE_CODE, SSS_INVITE_CODE)
                        noErrors()
                    }
                }

                //--------------------------------------------------------------------------------------------------
                private fun validateNewInviteStatus() {
                    rab.findEntityStatus(
                        invite.value.entityType,
                        request.status,
                        SSS_INVITE_STATUS
                    ) { newStatus4update.init(it) }

                    if (newStatus4update.isInitialized()) {
                        rab.validateEntityUpdateStatus(
                            invite.value,
                            newStatus4update.value,
                            SSS_INVITE_STATUS
                        )
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun findModifiedInvite() {
                    with(request) {
                        inviteService.findInviteByCode(inviteCode)
                            ?.apply {
                                invite.init(this)
                                validateNewInviteStatus()
                            }

                        if (invite.isNotInitialized()) let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_PLAYER_OLD_LOGIN,
                                findI18nMessage(FLD_UNKNOWN_INVITE_CODE, inviteCode)
                            )
                        }
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = inviteService.takeIf { invite.isInitialized() }
                    ?.apply {
                        setInviteNewStatus(
                            invite.value,
                            newStatus4update.value
                        )
                        executeAction(invite.value, EA_UPDATE_INVITE_STATUS, remoteAddress, request.toString())
                    }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setInviteCode(invite.value.inviteCode)
                        .setNewStatus(newStatus4update.value.entityStatusName)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}