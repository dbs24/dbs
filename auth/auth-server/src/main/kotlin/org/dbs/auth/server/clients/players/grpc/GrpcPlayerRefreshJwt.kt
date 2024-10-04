package org.dbs.auth.server.clients.players.grpc


import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.StringFuncs.firstAndLast10
import org.dbs.auth.server.enums.ApplicationEnum.CHESS_COMMUNITY
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.AuthServiceLayer.Companion.jwtStorageService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.playerClientService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.playersSecurityService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_USER_AGENT
import org.dbs.consts.IpAddress
import org.dbs.consts.PrivilegeCode
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.player.PlayerLogin
import org.dbs.player.PlayersConsts.Claims.CL_PLAYER_LOGIN
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.inTransaction
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.*
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.auth.RefreshPlayerJwtRequest as REQ
import org.dbs.protobuf.core.JwtsExpiry as ENT
import org.dbs.protobuf.core.MainResponse as RESP

object GrpcPlayerRefreshJwt {
    suspend fun AuthServerGrpcService.playerRefreshJwtInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
        userAgent: String = CK_USER_AGENT.get()
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        val entityBuilder by lazy { ENT.newBuilder() }
        val playerLogin by lazy { LateInitVal<PlayerLogin>() }
        val issuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val refreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val existsExpiredIssuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val existsRefreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val existsPlayerPrivileges by lazy { CollectionLateInitVal<PrivilegeCode>() }

        buildGrpcResponseOld {
            it.run {
                //======================================================================================================
                fun validateRequestBody(): Boolean = run {
                    // validate access jwt
                    jwts.accessJwt.ifEmpty {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            FLD_ACCESS_JWT,
                            "access jwt not specified"
                        )
                    }
                    // validate refresh jwt
                    jwts.refreshJwt.ifEmpty {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            FLD_REFRESH_JWT,
                            "refresh jwt not specified"
                        )
                    }

                    // validate player login from Jwt
                    jwtSecurityService.getClaim(
                        jwts.refreshJwt,
                        CL_PLAYER_LOGIN
                    )?.let {
                        playerLogin.init(it)
                    } ?: run {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            SSS_PLAYER_LOGIN,
                            "unknown or invalid player login in jwt claims [${jwts.refreshJwt.firstAndLast10()}]"
                        )
                    }
                    noErrors()
                }

                fun validatePlayerCredentials(): Mono<RAB> =
                    playerClientService.getAndValidatePlayerCredentials(playerLogin.value, it).run {
                        // no errors
//                        if (responseAnswer.hasResponseEntity()) {
//                            logger.debug { "+++ validatePlayerCredentials "}
//                        }

                        it.takeIf { it.noErrors() }.toMono()
                    }.switchIfEmpty {
                        it.apply {
                            if (noErrors()) {
                                it.addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    SSS_PLAYER_LOGIN,
                                    "unknown player login '${{ playerLogin.value }}'"
                                )
                            }
                        }
                        empty()
                    }

                fun createPlayerJwt(): Mono<IssuedJwt> =
                    playersSecurityService.createPlayerJwt(
                        userAgent,
                        playerLogin.value,
                        remoteAddress,
                        existsPlayerPrivileges.value
                    )
                        .map { it.also { issuedJwt.init(it) } }

                fun createPlayerRefreshJwt(issuedJwt: IssuedJwt) =
                    playersSecurityService.createPlayerRefreshJwt(issuedJwt.jwtId, playerLogin.value, userAgent, remoteAddress)
                        .map { it.also { refreshJwt.init(it) } }

                fun moveObsoletJwtsToArc(newRefreshJwt: RefreshJwt) = newRefreshJwt.run {
                    jwtStorageService.moveJwt2Arc(existsExpiredIssuedJwt.value)
                        .flatMap { jwtStorageService.moveRefreshJwt2Arc(existsRefreshJwt.value) }
                        .then(newRefreshJwt.toMono())
                }

                fun Mono<RAB>.validateExpiredAccessJwt() = flatMap { ab ->
                    jwtStorageService.findActualJwt(it, jwts.accessJwt)
                        .map { existsExpiredIssuedJwt.init(it); ab }
                }

                fun Mono<RAB>.validateExistsRefreshJwt() = flatMap { ab ->
                    jwtStorageService.findRefreshJwt(it, jwts.refreshJwt)
                        .map {
                            existsRefreshJwt.init(it)

                            if (existsRefreshJwt.value.parentJwtId != existsExpiredIssuedJwt.value.jwtId) {
                                addErrorInfo(
                                    RC_INVALID_RESPONSE_DATA,
                                    INVALID_ENTITY_ATTR,
                                    FLD_REFRESH_JWT,
                                    "invalid token pair (issuedJwtId = ${existsExpiredIssuedJwt.value.jwtId}," +
                                            " RefreshJwt.parentJwtId = ${existsRefreshJwt.value.parentJwtId}), " +
                                            "applied refreshJwt.jwtId = ${existsRefreshJwt.value.jwtId}"
                                )
                            }
                            ab
                        }
                }

                fun Mono<RAB>.validatePlayerPrivileges() = map { // TODO not finished?
//                    actorsClientService.getPlayerPrivileges(playerLogin.value, response)
//                        .map {
//                            it.responseEntity?.run { existsPlayerPrivileges.hold(playerPrivileges) }
//                            response
//                        }
                    it
                }

                fun Mono<RAB>.createAndSaveNewJwts() = flatMap { ab ->
                    inTransaction {
                        jwtStorageService.revokeExistsJwt(
                            playerLogin.value,
                            CHESS_COMMUNITY
                        )
                            .then(createPlayerJwt())
                            .flatMap(::createPlayerRefreshJwt)
                            .flatMap(::moveObsoletJwtsToArc)
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

                if (validateRequestBody()) {
                    processGrpcResponse {
                        validatePlayerCredentials()
                            .validatePlayerPrivileges()
                            .validateExpiredAccessJwt()
                            .validateExistsRefreshJwt()
                            .createAndSaveNewJwts()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }
    }
}
