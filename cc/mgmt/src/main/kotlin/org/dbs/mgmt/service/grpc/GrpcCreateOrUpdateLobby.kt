package org.dbs.mgmt.service.grpc

import org.dbs.api.CommonJobs.JK_SAVE
import org.dbs.api.JobKey
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.consts.EntityCode
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.enums.I18NEnum.EXIST_LOBBY_CODE
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_LOBBY_CODE
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.mgmt.mapper.LobbyMappers.updateLobby
import org.dbs.mgmt.model.lobby.Lobby
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.lobbyService
import org.dbs.mgmt.service.grpc.GrpcCreateOrUpdateLobby.JobKeyImp.JK_FIND_OR_CREATE_LOBBY
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateOptionalField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.SSS_LOBBY_CODE
import org.dbs.validator.Field.SSS_LOBBY_OLD_CODE
import org.dbs.mgmt.client.CreateOrUpdateLobbyRequest as REQ
import org.dbs.mgmt.client.CreatedLobbyDto as ENT

object GrpcCreateOrUpdateLobby {

    private enum class JobKeyImp : JobKey {
        JK_FIND_OR_CREATE_LOBBY,
    }

    suspend fun MgmtGrpcService.createOrUpdateLobbyInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    validateNewLobbyCode()
                    findOrCreateLobby()
                    saveEntity()
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                private val lobby by lazy { LateInitVal<Lobby>("lobby") }
                private val lobby4update by lazy { LateInitVal<Lobby>("lobby4update") }
                private val oldLobbyCode by lazy { request.oldLobbyCode.grpcGetOrNull() }

                //======================================================================================================
                override fun isValidDto() = request.run {
                    with(rab) {
                        validateMandatoryField(lobbyCode, LOGIN_PATTERN, SSS_LOBBY_CODE)
                        validateOptionalField(oldLobbyCode, LOGIN_PATTERN, SSS_LOBBY_OLD_CODE)
                        noErrors()
                    }
                }

                //------------------------------------------------------------------------------------------------------
                private suspend fun validateNewLobbyCode(newCode: EntityCode) =
                    lobbyService.findLobbyByLogin(newCode)
                        ?.apply {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_LOBBY_CODE,
                                "${findI18nMessage(EXIST_LOBBY_CODE)} '$newCode'"
                            )
                        }

                //------------------------------------------------------------------------------------------------------
                private fun updateFromDto(lobby: Lobby): Lobby =
                    lobbyService.updateLobby(lobby, request)
                        .run { lobby4update.init(this) }

                //------------------------------------------------------------------------------------------------------
                private suspend fun saveLobby(lobby: Lobby): Lobby = lobbyService.saveLobby(lobby)

                //------------------------------------------------------------------------------------------------------
                suspend fun validateNewLobbyCode() =
                    request.apply {
                        val checkNewCode = oldLobbyCode?.let { it != lobbyCode } != false
                        if (checkNewCode) validateNewLobbyCode(lobbyCode)
                    }

                //------------------------------------------------------------------------------------------------------
                suspend fun findOrCreateLobby() = launchJob(JK_FIND_OR_CREATE_LOBBY) {
                    lobbyService.findLobbyByLogin(oldLobbyCode ?: request.lobbyCode)
                        ?.apply { lobby.init(this) }
                        ?: oldLobbyCode?.let {
                            rab.addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                SSS_LOBBY_OLD_CODE,
                                findI18nMessage(FLD_UNKNOWN_LOBBY_CODE, it)
                            )
                        } ?: lobbyService.createNewLobby(request.lobbyCode)
                            .also { lobby.init(it) }
                }

                //------------------------------------------------------------------------------------------------------
                suspend fun saveEntity() = launchJob(JK_SAVE, JK_FIND_OR_CREATE_LOBBY) {
                    lobbyService.saveHistory(lobby.value)
                    saveLobby(updateFromDto(lobby.value))
                }

                //------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(JK_SAVE, ENT.newBuilder()) {
                    it.setLobbyCode(lobby4update.value.lobbyCode)
                        .setStatus(lobby4update.value.entityStatus.entityStatusName)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
