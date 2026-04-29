package org.dbs.mgmt.service.grpc

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_LOBBY_CODE
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.mgmt.model.lobby.Lobby
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.lobbyService
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.findEntityStatus
import org.dbs.service.validator.GrpcValidators.validateEntityUpdateStatus
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.SSS_LOBBY_CODE
import org.dbs.validator.Field.SSS_LOBBY_STATUS
import org.dbs.mgmt.client.CreatedLobbyStatus as ENT
import org.dbs.mgmt.client.UpdateLobbyStatusRequest as REQ
import org.dbs.protobuf.core.MainResponse as RESP

object GrpcUpdateLobbyStatus {

    suspend fun MgmtGrpcService.updateLobbyStatusInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findModifiedLobby()
                    saveEntity()
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                private val lobby by lazy { LateInitVal<Lobby>("lobby") }
                val newStatus4update by lazy { LateInitVal<EntityStatusEnum>() }

                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(modifiedCode, LOGIN_PATTERN, SSS_LOBBY_CODE)
                        noErrors()
                    }
                }

                //--------------------------------------------------------------------------------------------------
                private fun validateNewLobbyStatus() {
                    rab.findEntityStatus(
                        lobby.value.entityType,
                        request.status,
                        SSS_LOBBY_STATUS
                    ) { newStatus4update.init(it) }

                    if (newStatus4update.isInitialized()) {
                        rab.validateEntityUpdateStatus(
                            lobby.value,
                            newStatus4update.value,
                            SSS_LOBBY_STATUS
                        )
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun findModifiedLobby() {
                    with(request) {
                        lobbyService.findLobbyByLogin(modifiedCode)
                            ?.apply {
                                lobby.init(this)
                                validateNewLobbyStatus()
                            }

                        if (lobby.isNotInitialized()) let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_LOBBY_CODE,
                                findI18nMessage(FLD_UNKNOWN_LOBBY_CODE, modifiedCode)
                            )
                        }
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = lobbyService.takeIf { lobby.isInitialized() }
                    ?.apply {
                        val updated = setLobbyNewStatus(lobby.value, newStatus4update.value)
                        saveLobby(updated)
                    }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setModifiedCode(lobby.value.lobbyCode)
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
