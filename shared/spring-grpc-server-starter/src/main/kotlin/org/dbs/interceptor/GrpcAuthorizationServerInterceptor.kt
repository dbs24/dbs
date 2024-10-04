package org.dbs.interceptor

import io.grpc.*
import io.grpc.Status.OK
import io.jsonwebtoken.ExpiredJwtException
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.GrpcConsts.ContextKeys.CK_GRPC_CONTEXT
import org.dbs.consts.GrpcConsts.ContextKeys.CK_INTERCEPTOR_EXCEPTION
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_SERVER_REFLECTION_INFO
import org.dbs.consts.GrpcConsts.ContextKeys.CK_USER_AGENT
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_BEARER_AUTHORIZATION
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_IS_AUTHORIZED
import org.dbs.consts.GrpcConsts.MetadataKeys.JWT_AUTHORIZATION_FAIL
import org.dbs.consts.IpAddress
import org.dbs.consts.Jwt
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.enums.I18NEnum.JWT_WAS_EXPIRED
import org.dbs.ext.GrpcFuncs.getProcedureName
import org.dbs.ext.GrpcFuncs.getRemoteAddress
import org.dbs.ext.GrpcFuncs.getUserAgent
import org.dbs.ext.GrpcFuncs.log
import org.dbs.service.GrpcYmlConfig
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.JwtValidator
import org.dbs.service.core.AbstractGrpcServiceInternal
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.security.api.JwtSecurityServiceApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@GrpcGlobalServerInterceptor
@ConditionalOnProperty(name = ["config.grpc.enable-outer-interceptor"])
class GrpcAuthorizationServerInterceptor(
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private val grpcServiceInternal: AbstractGrpcServiceInternal
) : AbstractApplicationService(), ServerInterceptor {
    val jwtSecurityService: JwtSecurityServiceApi by lazy { findService(JwtSecurityServiceApi::class) }
    val ymlConfig by lazy { findCanonicalService(GrpcYmlConfig::class) }
    val jwtValidator: JwtValidator = { service, jwt ->
        GRPC_IS_AUTHORIZED.takeIf { service.validateJwt(jwt) } ?: JWT_AUTHORIZATION_FAIL
    }
    val allowedPoints = CollectionLateInitVal<String>()

    override fun initialize() {
        super.initialize()
        allowedPoints.init(
            getAllowedProcedures()
                .map { it.toString() }
                .toSet()
        )
    }

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): ServerCall.Listener<ReqT> = call.run {
        val (grpcProcedure, remoteAddress, userAgent) = logH2Call(headers)

        val jwtStorage = LateInitVal<Jwt>()
        val callContext: Context = runCatching {
            checkGrpcReflection(grpcProcedure)
            if (isWhiteProcedure(grpcProcedure)) {
                Context.current()
            } else
                executeInternalInterceptCallIfStatusIsOk(processBearerJwt(headers, jwtStorage), call, headers, jwtStorage, next)

        }.getOrElse { throwable -> handleException(throwable, grpcProcedure) }

        Contexts.interceptCall(
            callContext
                .withValue(CK_REMOTE_ADDRESS, remoteAddress)
                .withValue(CK_USER_AGENT, userAgent)
                .withValue(CK_GRPC_CONTEXT, STRING_TRUE),
            call,
            headers,
            next
        ).run { RequestLogger(grpcProcedure, this) }
    }

    private fun checkGrpcReflection(grpcProcedure: String) {
        if (grpcProcedure == CK_SERVER_REFLECTION_INFO.get()) {
            require(ymlConfig.enableGrpcReflection) { "gRPC reflection is not allowed" }
        }
    }

    private fun <ReqT, RespT> executeInternalInterceptCallIfStatusIsOk(
        status: Status,
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        jwtStorage: LateInitVal<Jwt>,
        next: ServerCallHandler<ReqT, RespT>
    ): Context = run {
        if (status != OK) {
            call.close(status, headers.also { it.put(JWT_AUTHORIZATION_FAIL, status.description) })
            logger.warn { "### call is cancelled = $call" }
        }
        if (jwtStorage.isInitialized() && !call.isCancelled) {
            grpcServiceInternal.interceptCallInternal(call, headers, next)
                .also { logger.debug { "call context = [${it.deadline}]" } }
        } else {
            Context.current()
                .also { logger.warn { "use currentContext, possible error" } }
        }
    }

    private fun handleException(throwable: Throwable, grpcProcedure: String): Context = run {
        Context.current().withValue(
            CK_INTERCEPTOR_EXCEPTION,
            handleAndGetErrorMessage(throwable, grpcProcedure)
        )
    }

    private fun isWhiteProcedure(procedureName: String): Boolean = allowedPoints.value.any { it == procedureName }

    private fun processBearerJwt(metadata: Metadata, jwtStorage: LateInitVal<Jwt>): Status =
        metadata[GRPC_BEARER_AUTHORIZATION]
            ?.run {
                logger.debug { "authenticate grpc call with [${last15()}]" }
                jwtStorage.init(getJwtFromBearer())

                jwtValidator(jwtSecurityService, jwtStorage.value).let {
                    if (it != GRPC_IS_AUTHORIZED) {
                        logger.warn { this }
                        Status.CANCELLED.withDescription(JWT_AUTHORIZATION_FAIL.name())
                    } else
                        OK
                }
            } ?: run {
            logger.warn { JWT_AUTHORIZATION_FAIL }
            Status.CANCELLED.withDescription(JWT_AUTHORIZATION_FAIL.name())
        }

    private fun getAllowedProcedures() =
        if (ymlConfig.enableGrpcReflection) {
            grpcServiceInternal.getWhiteProcedures().plus(CK_SERVER_REFLECTION_INFO)
        } else {
            grpcServiceInternal.getWhiteProcedures()
        }

    private fun handleAndGetErrorMessage(throwable: Throwable, grpcProcedure: String) =
        if (throwable is ExpiredJwtException) {
            findI18nMessage(JWT_WAS_EXPIRED).also {
                logger.warn { "$grpcProcedure: $it" }
            }
        } else throwable.toString().also {
            logger.error { "### $grpcProcedure: call exception: $throwable" }
            throwable.printStackTrace()
        }

    private fun <ReqT, RespT> ServerCall<ReqT, RespT>.logH2Call(headers: Metadata): Triple<String, IpAddress, String> = run {
        logger.info { "███ h2 request" }
        val grpcProcedure = getProcedureName()
        val remoteAddress = getRemoteAddress(grpcProcedure, headers)
        val userAgent = getUserAgent(grpcProcedure, headers)
        logger.info { "request from $authority($userAgent) [$remoteAddress] ==> [$grpcProcedure] " }
        logger.debug { headers.log() }
        logger.debug { log() }
        Triple(grpcProcedure, remoteAddress, userAgent)
    }

    inner class RequestLogger<ReqT>(private val grpcProcedure: String, delegate: ServerCall.Listener<ReqT>) :
        ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
        override fun onMessage(req: ReqT) {
            logger.debug {
                "h2: $grpcProcedure " +
                    "(${(req as Any).javaClass.canonicalName}'})\n$req"
            }
            super.onMessage(req)
        }
    }
}
