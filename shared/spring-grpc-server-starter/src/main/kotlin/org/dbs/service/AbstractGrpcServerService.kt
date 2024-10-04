package org.dbs.service

import com.google.protobuf.Any
import io.grpc.*
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener
import io.grpc.Status.ABORTED
import io.grpc.Status.OK
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import kotlinx.coroutines.yield
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.consts.CKS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_GRPC_CONTEXT
import org.dbs.consts.GrpcConsts.ContextKeys.CK_INTERCEPTOR_EXCEPTION
import org.dbs.consts.GrpcConsts.ContextKeys.CK_JWT_CLAIMS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_SERVER_REFLECTION_INFO
import org.dbs.consts.GrpcConsts.ContextKeys.CK_USER_AGENT
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_ABUSED_CONNECTION
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_BEARER_AUTHORIZATION
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_IS_AUTHORIZED
import org.dbs.consts.GrpcConsts.MetadataKeys.JWT_AUTHORIZATION_FAIL
import org.dbs.consts.GrpcConsts.Refletion.buildMethod
import org.dbs.consts.GrpcConsts.Refletion.builderMethod
import org.dbs.consts.GrpcConsts.Refletion.responseAnswerMethod
import org.dbs.consts.IpAddress
import org.dbs.consts.Jwt
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.enums.I18NEnum.*
import org.dbs.ext.GrpcFuncs.getRemoteAddress
import org.dbs.ext.GrpcFuncs.getUserAgent
import org.dbs.ext.GrpcFuncs.log
import org.dbs.ext.JwtGrpcExt.processBearerJwt
import org.dbs.ext.LoggerFuncs.logRequest
import org.dbs.grpc.consts.GM
import org.dbs.grpc.consts.GMBuilder
import org.dbs.grpc.consts.ProtobufAny
import org.dbs.grpc.consts.RESP
import org.dbs.grpc.ext.MessageV3.isNull
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.protobuf.core.MainResponse
import org.dbs.protobuf.core.ResponseCode
import org.dbs.protobuf.core.ResponseCode.*
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.isValidIp
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.validIpV4
import org.dbs.rest.service.Bucket4jRateLimitService
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.security.api.JwtSecurityServiceApi
import org.dbs.validator.Error
import org.dbs.validator.Error.*
import org.dbs.validator.Field
import org.dbs.validator.Field.JWT_TOKEN
import org.dbs.validator.Field.UNKNOWN_FIELD
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.jvm.kotlinFunction
import kotlin.system.measureTimeMillis

abstract class AbstractGrpcServerService : GrpcServerServiceApi, AbstractApplicationService() {

    val ymlConfig by lazy { findCanonicalService(GrpcYmlConfig::class) }
    val b4jService by lazy { findService(Bucket4jRateLimitService::class) }
    val grpcResponseBuilder: GrpcResponse<MainResponse> by lazy {
        {
            MainResponse.newBuilder().setResponseAnswer(it).build()
        }
    }

    // default interceptor
    override fun <ReqT, RespT> interceptCallInternal(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): Context = let {
        logger.debug { "outer interceptCallInternal; metadata=$headers" }
        Context.current().addClaims(headers)
    }

    private fun Context.addClaims(headers: Metadata): Context = this.withValue(CK_JWT_CLAIMS, headers.getJwtClaims())

    override val whiteProcs = setOf<CKS>()

    val whiteProcsString by lazy {
        (whiteProcs
            // enable reflection
            .takeUnless { ymlConfig.enableGrpcReflection } ?: whiteProcs.plus(CK_SERVER_REFLECTION_INFO))
            .map { it.toString() }
            .toSet()
    }

    override val jwtSecurityService: JwtSecurityServiceApi by lazy { findService(JwtSecurityServiceApi::class) }

    // overridable - default jwt validator
    override val jwtValidator: JwtValidator = { service, jwt ->
        GRPC_IS_AUTHORIZED.takeIf { service.validateJwt(jwt) } ?: JWT_AUTHORIZATION_FAIL
    }

    override fun validateRemoteAddress(ip: IpAddress) {
        require(ip.isValidIp()) { findI18nMessage(INVALID_IP_ADDRESS, ip) }
    }

    fun List<CKS>.isContainProcedure(procedureName: String): Boolean =
        this.any { it.toString() == procedureName }

    fun <ReqT, RespT> ServerCall<ReqT, RespT>.getProcedureName(): String = methodDescriptor.bareMethodName
        ?.also {
            require(it.length > 3) { "${findI18nMessage(GRPC_PROCEDURE_NAME_IS_NOT_ASSIGNED)} " }
        } ?: error(findI18nMessage(GRPC_PROCEDURE_NAME_IS_NOT_ASSIGNED))

    fun Metadata.getJwtClaims(): Claims = get(GRPC_BEARER_AUTHORIZATION)
        ?.run {
            jwtSecurityService.getAllClaimsFromJwt(getJwtFromBearer())
        } ?: error(findI18nMessage(JWT_IS_MISSING))

    fun Metadata.getJwtClaim(claimName: String): String = get(GRPC_BEARER_AUTHORIZATION)
        ?.run {
            jwtSecurityService.getClaim(getJwtFromBearer(), claimName)
        } ?: error("$claimName: ${findI18nMessage(JWT_CLAIM_IS_MISSING)}")

    //==================================================================================================================
    @Suppress(UNCHECKED_CAST)
    suspend inline fun <B : GMBuilder<B>, R : GM> buildGrpcResponse(entityResponseBuilder: EntityResponseBuilder0<R, B>): RESP =
        RA.newBuilder().run {
            runCatching {
                val resp: RESP
                measureTimeMillis {
                    resp = run {
                        val responseEntity: R = CK_INTERCEPTOR_EXCEPTION.get()?.run {
                            addErrorInfo(
                                RC_INTERNAL_ERROR,
                                GRPC_INTERNAL_ERROR,
                                UNKNOWN_FIELD,
                                this
                            )
                            null
                        } ?: run { entityResponseBuilder(this).execute().build() as R }

                        (if (noErrors()) {
                            setResponseEntity(Any.pack(responseEntity))
                        } else {
                            logger.warn { "errors count: $errorMessagesCount" }
                        })

                        (grpcResponseBuilder
                            (build()))
                            .also {
                                logger.info {
                                    (CK_GRPC_CONTEXT.get()?.let { "▒▒▒ h2 response: " } ?: EMPTY_STRING) +
                                            "${responseEntity.javaClass.canonicalName}:\n $responseEntity"
                                }
                                if (noErrors()) {
                                    if (responseEntity.isNull()) logger.warn {
                                        "${responseEntity.javaClass.canonicalName}: " +
                                                "responseEntity is not specified or body is empty " +
                                                "(serializedSize = ${responseEntity.serializedSize})"
                                    }
                                    if (responseCode == UNRECOGNIZED) {
                                        setResponseCode(RC_OK)
                                    }
                                }
                            }
                    }
                }.also {
                    logger.logRequest(
                        it,
                        ymlConfig.maxTimeExec
                    ) { "${resp.javaClass.simpleName}: ${responseEntity.value}" }
                }
                resp
            }.getOrElse {

                yield()
                val rc by lazy { LateInitVal<ResponseCode>() }
                val error by lazy { LateInitVal<Error>() }
                val fld by lazy { LateInitVal<Field>() }
                val errMsg by lazy { LateInitVal<String>() }
                var cancelled = false

                when (it) {
                    is CancellationException -> {
                        logger.warn { "request was canceled: ${it.message}" }
                        cancelled = true
                    }

                    is StatusException -> {

                        if (it.message?.contains(JWT_AUTHORIZATION_FAIL.name()) == true) {
                            rc.init(RC_UNAUTHORIZED)
                            error.init(GRPC_JWT_IS_NOT_AUTHORIZED)
                            fld.init(JWT_TOKEN)
                            errMsg.init(JWT_AUTHORIZATION_FAIL.name())
                        } else {
                            rc.init(RC_SERVICE_UNAVAILABLE)
                            error.init(GRPC_SERVICE_IS_UNAVAILABLE)
                            fld.init(UNKNOWN_FIELD)
                            errMsg.init("Service unavailable")
                        }
                    }

                    else -> {
                        it.printStackTrace()
                        rc.init(RC_INTERNAL_ERROR)
                        error.init(GRPC_INTERNAL_ERROR)
                        fld.init(UNKNOWN_FIELD)
                        errMsg.init(it.message ?: it.toString())
                    }
                }

                logger.warn { it }

                if (!cancelled) {
                    addErrorInfo(rc.value, error.value, fld.value, errMsg.value)
                    logger.warn { "Failed: [${errMsg.value}]" }
                }

                logger.info {
                    "▒▒▒▒▒▒ error response: ${responseEntity.javaClass.canonicalName}:\n $responseEntity"
                }
                grpcResponseBuilder(build())
            }
        }

    @Deprecated("refactor 2 buildGrpcResponse method")
    @Suppress(UNCHECKED_CAST)
    suspend inline fun <B : GMBuilder<B>, R : GM> buildGrpcResponseOld(entityResponseBuilder: EntityResponseBuilder<B>): R =
        RA.newBuilder().run {
            runCatching {
                val resp: R
                measureTimeMillis {
                    resp = run {
                        val responseEntity: R = CK_INTERCEPTOR_EXCEPTION.get()?.run {
                            addErrorInfo(
                                RC_INTERNAL_ERROR,
                                GRPC_INTERNAL_ERROR,
                                UNKNOWN_FIELD,
                                this
                            )
                            null
                        } ?: entityResponseBuilder(this).build() as R

                        (if (noErrors()) {
                            setResponseEntity(Any.pack(responseEntity))
                        } else {
                            logger.warn { "errors count: $errorMessagesCount" }
                        })

                        ((grpcResponseBuilder
                            (build())) as R)
                            .also {
                                logger.info {
                                    (CK_GRPC_CONTEXT.get()?.let { "▒▒▒ h2 response: " } ?: EMPTY_STRING) +
                                            "${responseEntity.javaClass.canonicalName}:\n $responseEntity"
                                }
                                if (noErrors()) {
                                    if (responseEntity.isNull()) logger.warn {
                                        "${responseEntity.javaClass.canonicalName}: " +
                                                "responseEntity is not specified or body is empty " +
                                                "(serializedSize = ${responseEntity.serializedSize})"
                                    }
                                    if (responseCode == UNRECOGNIZED) {
                                        setResponseCode(RC_OK)
                                    }
                                }
                            }
                    }
                }.also {
                    logger.logRequest(
                        it,
                        ymlConfig.maxTimeExec
                    ) { "${resp.javaClass.simpleName}: ${responseEntity.value}" }
                }
                resp
            }.getOrElse {

                yield()
                val rc by lazy { LateInitVal<ResponseCode>() }
                val error by lazy { LateInitVal<Error>() }
                val fld by lazy { LateInitVal<Field>() }
                val errMsg by lazy { LateInitVal<String>() }
                var cancelled = false

                when (it) {
                    is CancellationException -> {
                        logger.warn { "request was canceled: ${it.message}" }
                        cancelled = true
                    }

                    is StatusException -> {

                        if (it.message?.contains(JWT_AUTHORIZATION_FAIL.name()) == true) {
                            rc.init(RC_UNAUTHORIZED)
                            error.init(GRPC_JWT_IS_NOT_AUTHORIZED)
                            fld.init(JWT_TOKEN)
                            errMsg.init(JWT_AUTHORIZATION_FAIL.name())
                        } else {
                            rc.init(RC_SERVICE_UNAVAILABLE)
                            error.init(GRPC_SERVICE_IS_UNAVAILABLE)
                            fld.init(UNKNOWN_FIELD)
                            errMsg.init("Service unavailable")
                        }
                    }

                    else -> {
                        it.printStackTrace()
                        rc.init(RC_INTERNAL_ERROR)
                        error.init(GRPC_INTERNAL_ERROR)
                        fld.init(UNKNOWN_FIELD)
                        errMsg.init(it.message ?: it.toString())
                    }
                }

                logger.warn { it }

                if (!cancelled) {
                    addErrorInfo(rc.value, error.value, fld.value, errMsg.value)
                    logger.warn { "Failed: [${errMsg.value}]" }
                }

                logger.info {
                    "▒▒▒▒▒▒ error response: ${responseEntity.javaClass.canonicalName}:\n $responseEntity"
                }
                grpcResponseBuilder(build()) as R
            }
        }

    override fun initialize() = super.initialize().also { logger.debug { "gRPC white procedures: $whiteProcs" } }

    @GrpcGlobalServerInterceptor
    inner class GrpcAuthorizationServerInnerInterceptor : AbstractApplicationService(), ServerInterceptor {

        inner class RequestLogger<ReqT>(private val grpcProcedure: String, delegate: ServerCall.Listener<ReqT>) :
            SimpleForwardingServerCallListener<ReqT>(delegate) {
            override fun onMessage(req: ReqT) {
                logger.debug {
                    "h2: $grpcProcedure " +
                            "(${(req as kotlin.Any).javaClass.canonicalName}'})\n$req"
                }
                super.onMessage(req)
            }
        }

        override fun <ReqT, RespT> interceptCall(
            call: ServerCall<ReqT, RespT>,
            headers: Metadata,
            next: ServerCallHandler<ReqT, RespT>,
        ): ServerCall.Listener<ReqT> = call.run {
            val jwtStorage = LateInitVal<Jwt>()
            val grpcProcedure = getProcedureName()
            val remoteAddress = getRemoteAddress(grpcProcedure, headers)
            val userAgent = getUserAgent(grpcProcedure, headers)
            val ipV4 = remoteAddress.validIpV4()

            logger.info { "███ h2 request from $authority($userAgent) [$remoteAddress] ==> [$grpcProcedure]" }
            logger.debug { headers.log() }
            logger.debug { log() }

            val callContext: Context = runCatching {

                if (false.takeUnless { ymlConfig.bucket4jEnabled } ?: !b4jService.validateRateLimit(ipV4)) {
                    val errDescription = "${GRPC_ABUSED_CONNECTION.name()} from $ipV4"
                    val status = ABORTED.withDescription(errDescription)

                    logger.warn { "### abused query, call is cancelled = $call" }
                    call.close(status, headers.also { it.put(GRPC_ABUSED_CONNECTION, errDescription) })

                    Context.current()
                        .also { logger.warn { errDescription } }

                } else {

                    if (grpcProcedure == CK_SERVER_REFLECTION_INFO.toString()) {
                        require(ymlConfig.enableGrpcReflection) { "gRPC reflection is not allowed" }
                    }

                    // white procedure
                    if (isWhiteProcedure(grpcProcedure)) {
                        Context.current()
                    } else
                    // JWT process
                        processBearerJwt(headers, jwtStorage, grpcProcedure).let { status ->
                            if (status != OK) {
                                call.close(status, headers.also { it.put(JWT_AUTHORIZATION_FAIL, status.description) })
                                logger.warn { "### call is cancelled = $call" }
                            }

                            if (jwtStorage.isInitialized() && !call.isCancelled)
                                interceptCallInternal(call, headers, next)
                                    .also { logger.debug { "call context = [${it.deadline}]" } }
                            else
                                Context.current()
                                    .also { logger.warn { "use currentContext, possible error" } }
                        }
                }
            }.getOrElse { throwable ->

                val errMsg = if (throwable is ExpiredJwtException) {
                    findI18nMessage(JWT_WAS_EXPIRED).also {
                        logger.warn { "$grpcProcedure: $it" }
                    }
                } else throwable.toString().also {
                    logger.error { "### $grpcProcedure: call exception: $throwable" }
                    throwable.printStackTrace()
                }
                Context.current().withValue(CK_INTERCEPTOR_EXCEPTION, errMsg)
            }

            Contexts.interceptCall(
                callContext.withValue(CK_REMOTE_ADDRESS, remoteAddress)
                    .withValue(CK_GRPC_CONTEXT, STRING_TRUE)
                    .withValue(CK_USER_AGENT, userAgent),
                call,
                headers,
                next
            ).run { RequestLogger(grpcProcedure, this) }
        }

        private fun isWhiteProcedure(procedureName: String): Boolean = whiteProcsString.any { it == procedureName }
    }

    companion object : Logging {

        @Suppress(UNCHECKED_CAST)
        fun <T : GM> buildResponseAnswer(respClass: Class<T>, responseEntity: ProtobufAny): T =
            buildResponseAnswer(
                respClass, RA.newBuilder()
                    .setResponseCode(RC_OK)
                    .setResponseEntity(responseEntity)
                    .build()
            )

        @Suppress(UNCHECKED_CAST)
        fun <T : GM> buildErrorResponseAnswer(respClass: Class<T>, throwable: Throwable): T =
            buildResponseAnswer(respClass, throwable.toString().let {
                RA.newBuilder()
                    .setResponseCode(RC_INTERNAL_ERROR)
                    .setErrorMessage(it)
                    .addErrorInfo(it)
                    .build()
            })

        @Suppress(UNCHECKED_CAST)
        private fun <T : GM> buildResponseAnswer(respClass: Class<T>, responseAnswer: RA): T = run {
            // new builder
            val kBuilder = (respClass.getMethod(builderMethod).kotlinFunction
                ?: error("method '$builderMethod' not found ($respClass) ")).call()
            val buildPrepare = ((kBuilder?.javaClass)?.getMethod(responseAnswerMethod, RA::class.java)?.kotlinFunction
                ?: error("method '$responseAnswerMethod' not found (${kBuilder?.javaClass}) "))
                .call(kBuilder, responseAnswer)
            // build
            val kBuildMethod = (buildPrepare?.javaClass)?.getMethod(buildMethod)?.kotlinFunction
                ?: error("method '$buildMethod' not found (${buildPrepare?.javaClass}) ")
            kBuildMethod.call(buildPrepare) as T
        }
    }
}
