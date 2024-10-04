package org.dbs.auth.server.clients.players.grpc


import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.auth.server.enums.ApplicationEnum.CHESS_COMMUNITY
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.AuthServiceLayer.Companion.playerClientService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.playersSecurityService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_USER_AGENT
import org.dbs.consts.IpAddress
import org.dbs.consts.PrivilegeCode
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_PLAYER_LOGIN
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_PLAYER_LOGIN_OR_PASSWORD
import org.dbs.grpc.ext.ResponseAnswerObj.hasErrors
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.grpc.ext.ResponseAnswerObj.unpackResponseEntity
import org.dbs.mgmt.client.PlayerCredentials
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.inTransaction
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validatePasswordField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Error.PLAYER_PASSWORDS_MISMATCH
import org.dbs.validator.Field.SSS_PLAYER_LOGIN
import org.dbs.validator.Field.SSS_PLAYER_PASSWORD
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.auth.PlayerLoginRequest as REQ
import org.dbs.protobuf.core.JwtsExpiry as ENT
import org.dbs.protobuf.core.MainResponse as RESP

object GrpcPlayerLogin {
    suspend fun AuthServerGrpcService.loginPlayerInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
        userAgent: String = CK_USER_AGENT.get()
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        val entityBuilder by lazy { ENT.newBuilder() }
        val issuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val refreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val existsPlayerPrivileges by lazy { CollectionLateInitVal<PrivilegeCode>() }

        buildGrpcResponseOld {
            it.run {
                //======================================================================================================
                fun validateRequestData(): Boolean = run {

                    playerLogin.takeUnless { it == ROOT_USER }
                        ?.run {
                            validateMandatoryField(
                                playerLogin,
                                LOGIN_PATTERN,
                                SSS_PLAYER_LOGIN
                            )
                        }
                    validatePasswordField(playerPassword)
                    noErrors()
                }

                //======================================================================================================
                fun validatePlayerCredentials(): Mono<RAB> =
                    playerClientService.getAndValidatePlayerCredentials(playerLogin, it).run {
                        if (!hasErrors())
                            with(responseAnswer.unpackResponseEntity<PlayerCredentials>()) {

                                // match passwords
                                if (!passwordEncoder.matches(
                                        request.playerPassword,
                                        playerPassword
                                    )
                                ) {
                                    it.addErrorInfo(
                                        RC_INVALID_RESPONSE_DATA,
                                        PLAYER_PASSWORDS_MISMATCH,
                                        SSS_PLAYER_PASSWORD,
                                        findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN_OR_PASSWORD)
                                    )
                                }
                            }

                        it.takeIf { it.noErrors() }.toMono()
                    }.switchIfEmpty {
                        it.apply {
                            if (noErrors()) {
                                it.addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    SSS_PLAYER_LOGIN,
                                    findI18nMessage(FLD_UNKNOWN_PLAYER_LOGIN, request.playerLogin)
                                )
                            }
                        }
                        empty()
                    }

                fun Mono<RAB>.validatePlayerPrivileges() = map { // TODO not finished
//                    bankingCoreClient.getPlayerPrivileges(requestBody.userLogin, response)
//                        .map {
//                            it.responseEntity?.run { existsPlayerPrivileges = playerPrivileges }
//                            response
//                        }
                    it
                }

                fun createPlayerJwt(): Mono<IssuedJwt> =
                    playersSecurityService.createPlayerJwt(
                        playerLogin, userAgent, remoteAddress, existsPlayerPrivileges.value
                    )
                        .map { it.also { issuedJwt.init(it) } }

                fun createPlayerRefreshJwt(issuedJwt: IssuedJwt) =
                    playersSecurityService.createPlayerRefreshJwt(
                        issuedJwt.jwtId,
                        playerLogin,
                        userAgent,
                        remoteAddress
                    )
                        .map { it.also { refreshJwt.init(it) } }

                fun Mono<RAB>.createAndSaveNewJwts() = flatMap { ab ->
                    inTransaction {
                        playersSecurityService.revokeExistsJwt(playerLogin, CHESS_COMMUNITY)
                            .then(createPlayerJwt())
                            .flatMap(::createPlayerRefreshJwt)
                            .map { ab }
                    }
                }

                //======================================================================================================
                fun Mono<RAB>.finishResponseEntity() = map {
                    it.also {
                        playersSecurityService.apply {
                            entityBuilder
                                .setAccessJwt(buildAccessJwtsExp(issuedJwt.value.jwt))
                                .setRefreshJwt(buildRefreshJwtsExp(refreshJwt.value.jwt))
                        }
                    }
                }
                //======================================================================================================
                // validate requestData
                if (validateRequestData()) {
                    // process endpoint
                    processGrpcResponse {
                        validatePlayerCredentials()
                            .validatePlayerPrivileges()
                            .createAndSaveNewJwts()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }
    }
}
