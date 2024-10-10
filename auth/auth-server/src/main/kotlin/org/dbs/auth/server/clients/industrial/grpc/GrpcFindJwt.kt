package org.dbs.auth.server.clients.industrial.grpc


import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.Patterns.JWT_PATTERN
import org.dbs.application.core.service.funcs.StringFuncs.firstAndLast10
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_TOKEN_KIND
import org.dbs.auth.server.consts.AuthServerConsts.Claims.TK_REFRESH_TOKEN
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.service.AuthServiceLayer.Companion.jwtStorageService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.enums.I18NEnum.FLD_INVALID_JWT
import org.dbs.ext.ResponseCoProcessor
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.ext.executeIternal
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.FLD_REFRESH_JWT
import org.dbs.validator.Field.JWT_TOKEN
import java.time.LocalDateTime.now
import org.dbs.protobuf.auth.FindJwtRequest as REQ
import org.dbs.protobuf.auth.JwtIntrospect as ENT
import org.dbs.protobuf.core.MainResponse as RESP


object GrpcFindJwt {
    suspend fun AuthServerGrpcService.findJwt(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = run {
        validateRemoteAddress(remoteAddress)
        buildGrpcResponse {
            ResponseCoProcessorWrapper(object : ResponseCoProcessor<REQ, ENT.Builder> {
                // main flow
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                override suspend fun execute() = executeIternal {
                    findJwt()
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                private val issuedJwt by lazy { LateInitVal<IssuedJwt>("issuedJwt") }
                private var isRefreshToken = false

                //======================================================================================================
                override fun isValidDto() = request.run dto@{
                    with(rab) {
                        validateMandatoryField(
                            jwt,
                            JWT_PATTERN,
                            JWT_TOKEN
                        ) {
                            // found user
                            logger.debug("find jwt: '${jwt.last15()}'")
                        }
                        noErrors()
                    }
                }

                //------------------------------------------------------------------------------------------------------------------
                suspend fun findJwt() {
                    jwtStorageService.findJwt(request.jwt)
                        .awaitSingleOrNull()
                        ?.apply { issuedJwt.init(this) }
                        ?: apply {
                            // possible refresh token
                            jwtSecurityService.getClaim(
                                request.jwt,
                                CL_TOKEN_KIND
                            )?.let {
                                if (it == TK_REFRESH_TOKEN) {
                                    isRefreshToken = true
                                } else {
                                    rab.addErrorInfo(
                                        RC_INVALID_REQUEST_DATA,
                                        INVALID_ENTITY_ATTR,
                                        FLD_REFRESH_JWT,
                                        findI18nMessage(FLD_INVALID_JWT, request.jwt.firstAndLast10())
                                    )
                                }
                            } ?: run {
                                rab.addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    INVALID_ENTITY_ATTR,
                                    FLD_REFRESH_JWT,
                                    findI18nMessage(FLD_INVALID_JWT, request.jwt.firstAndLast10())
                                )
                            }
                        }
                }

                //--------------------------------------------------------------------------------------------------------------
                override suspend fun finishResponse(): ENT.Builder = finish(ENT.newBuilder()) {

                    val now = now()

                    // issuedJwt
                    if (issuedJwt.isInitialized()) {
                        with(issuedJwt.value
                            .also { logger.debug( "found jwt: $it") }) {
                            it.setActive(!isRevoked and (validUntil>now))
                            it.setUsername(issuedTo)
                            it.setIat(issueDate.toLong())
                            it.setExp(validUntil.toLong())
                        }
                    } else {
                        it.setActive(isRefreshToken)
                        it.setUsername(UNKNOWN)
                        //it.setIat(issueDate.toLong())
                        it.setExp(now.toLong())
                    }
                    it.setJwt(request.jwt.last15())
                }

                //------------------------------------------------------------------------------------------------------
                override val jobsMap by lazy { defaultJobsMap() }
                override val rab by lazy { it }
                override val coroutineScope by lazy { defaultCoroutineScope() }

            })
        }
    }
}
