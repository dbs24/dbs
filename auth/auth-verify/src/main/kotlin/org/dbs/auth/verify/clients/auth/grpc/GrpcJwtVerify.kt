package org.dbs.auth.verify.clients.auth.grpc


import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.application.core.service.funcs.Patterns.JWT_PATTERN
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.verify.clients.auth.api.JwtAttrs
import org.dbs.auth.verify.service.AuthServiceLayer.Companion.authServerClientService
import org.dbs.auth.verify.service.YmlService
import org.dbs.auth.verify.service.grpc.JwtVerifyGrpcService
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.Jwt
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.grpc.ext.ResponseAnswerObj.unpackResponseEntity
import org.dbs.protobuf.auth.FindJwtRequest
import org.dbs.protobuf.auth.JwtIntrospect
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Field.JWT_TOKEN
import java.time.LocalDateTime.now
import org.dbs.protobuf.auth.JwtVerifyResult as ENT
import org.dbs.protobuf.auth.VerifyJwtRequest as REQ
import org.dbs.protobuf.core.MainResponse as RESP

object GrpcJwtVerify {
    suspend fun JwtVerifyGrpcService.verifyJwtInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<FindJwtRequest, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    verifyJwt()
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                private val authClientResponse by lazy { LateInitVal<MainResponse>("mainResponse") }
                private var isVerified = false
                private val jwtLast15 by lazy { LateInitVal<Jwt>("jwtLast15") }
                private val verifiedJwt by lazy { LateInitVal<Jwt>("mainResponse") }

                //======================================================================================================
                override fun isValidDto() = request.run dto@{
                    with(rab) {

                        verifiedJwt.init(jwt.replace(BEARER, EMPTY_STRING))
                        jwtLast15.init(verifiedJwt.value.last15().substring(4))

                        validateMandatoryField(
                            verifiedJwt.value,
                            JWT_PATTERN,
                            JWT_TOKEN
                        ) {
                            // found player
                            logger.debug("validate jwt: '${jwtLast15.value}'")
                        }
                        noErrors()
                    }
                }

                //------------------------------------------------------------------------------------------------------------------
                suspend fun verifyJwt() {

                    cachedJwtList.findJwt(jwtLast15.value)
                        ?.apply {
                            // found in cache
                            val now = now()

                            logger.debug { "found in cache: $jwtLast15 ($validUntil)" }
                            if (validUntil > now) {
                                isVerified = true
                            } else {
                                cachedJwtList.removeObsoletedJwt()
                            }
                        } ?: apply {
                        // call authServerClientService
                        authServerClientService.introspectJwt(verifiedJwt.value, rab)
                            .apply { authClientResponse.init(this) }

                        require(authClientResponse.isInitialized()) { "Auth client response is not initialized" }
                        with(authClientResponse.value.responseAnswer.unpackResponseEntity<JwtIntrospect>()) {
                            isVerified = this.active

                            if (isVerified) {
                                logger.debug { "put to cache: $jwtLast15" }
                                cachedJwtList.addJwt(jwtLast15.value, JwtAttrs(exp.toLocalDateTime()))
                            }
                        }
                    }
                }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {
                    it.setVerified(isVerified)
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
