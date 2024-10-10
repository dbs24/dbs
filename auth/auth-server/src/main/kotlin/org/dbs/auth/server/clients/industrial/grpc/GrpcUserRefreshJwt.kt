package org.dbs.auth.server.clients.industrial.grpc


import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.StringFuncs.firstAndLast10
import org.dbs.auth.server.enums.ApplicationEnum.CHESS_COMMUNITY
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.AuthServiceLayer.Companion.jwtStorageService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.userClientService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.usersSecurityService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_USER_AGENT
import org.dbs.goods.UsersConsts.Claims.CL_USER_LOGIN
import org.dbs.consts.IpAddress
import org.dbs.consts.Login
import org.dbs.consts.PrivilegeCode
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
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
import org.dbs.protobuf.auth.RefreshUserJwtRequest as REQ
import org.dbs.protobuf.core.JwtsExpiry as ENT
import org.dbs.protobuf.core.MainResponse as RESP

object GrpcUserRefreshJwt {
    suspend fun AuthServerGrpcService.userRefreshJwtInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
        userAgent: String = CK_USER_AGENT.get()
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        val entityBuilder by lazy { ENT.newBuilder() }
        val userLogin by lazy { LateInitVal<Login>() }
        val issuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val refreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val existsExpiredIssuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val existsRefreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val existsUserPrivileges by lazy { CollectionLateInitVal<PrivilegeCode>() }

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

                    // validate user login from Jwt
                    jwtSecurityService.getClaim(
                        jwts.refreshJwt,
                        CL_USER_LOGIN
                    )?.let {
                        userLogin.init(it)
                    } ?: run {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            SSS_USER_LOGIN,
                            "unknown or invalid user login in jwt claims [${jwts.refreshJwt.firstAndLast10()}]"
                        )
                    }
                    noErrors()
                }

                fun validateUserCredentials(): Mono<RAB> =
                    userClientService.getAndValidateUserCredentials(userLogin.value, it).run {
                        // no errors
//                        if (responseAnswer.hasResponseEntity()) {
//                            logger.debug { "+++ validateUserCredentials "}
//                        }

                        it.takeIf { it.noErrors() }.toMono()
                    }.switchIfEmpty {
                        it.apply {
                            if (noErrors()) {
                                it.addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    SSS_USER_LOGIN,
                                    "unknown user login '${{ userLogin.value }}'"
                                )
                            }
                        }
                        empty()
                    }

                fun createUserJwt(): Mono<IssuedJwt> =
                    usersSecurityService.createUserJwt(
                        userLogin.value,
                        userAgent,
                        remoteAddress,
                        existsUserPrivileges.value
                    )
                        .map { it.also { issuedJwt.init(it) } }

                fun createUserRefreshJwt(issuedJwt: IssuedJwt) =
                    usersSecurityService.createUserRefreshJwt(issuedJwt.jwtId, userLogin.value, userAgent, remoteAddress)
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

                fun Mono<RAB>.validateUserPrivileges() = map { // TODO not finished?
//                    actorsClientService.getUserPrivileges(userLogin.value, response)
//                        .map {
//                            it.responseEntity?.run { existsUserPrivileges.hold(userPrivileges) }
//                            response
//                        }
                    it
                }

                fun Mono<RAB>.createAndSaveNewJwts() = flatMap { ab ->
                    inTransaction {
                        jwtStorageService.revokeExistsJwt(
                            userLogin.value,
                            CHESS_COMMUNITY
                        )
                            .then(createUserJwt())
                            .flatMap(::createUserRefreshJwt)
                            .flatMap(::moveObsoletJwtsToArc)
                            .map { ab }
                    }
                }

                //======================================================================================================
                fun Mono<RAB>.finishResponseEntity() = map {
                    it.also {
                        usersSecurityService.apply {
                            entityBuilder
                                .setAccessJwt(buildAccessJwtsExp(issuedJwt.value.jwt))
                                .setRefreshJwt(buildRefreshJwtsExp(refreshJwt.value.jwt))
                        }
                    }
                }

                if (validateRequestBody()) {
                    processGrpcResponse {
                        validateUserCredentials()
                            .validateUserPrivileges()
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
