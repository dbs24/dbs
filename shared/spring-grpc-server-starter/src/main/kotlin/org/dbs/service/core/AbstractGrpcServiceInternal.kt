package org.dbs.service.core

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.jsonwebtoken.Claims
import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.consts.CKS
import org.dbs.consts.GrpcConsts.ContextKeys.CK_JWT_CLAIMS
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_BEARER_AUTHORIZATION
import org.dbs.enums.I18NEnum.JWT_IS_MISSING
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.security.api.JwtSecurityServiceApi

/**
 * Core grpc class which overridable methods used in the interceptor
 */
abstract class AbstractGrpcServiceInternal : AbstractApplicationService() {
    private val whiteProcedures: List<CKS> = listOf()
    val jwtSecurityService by lazy { findService(JwtSecurityServiceApi::class) }

    open fun getWhiteProcedures(): List<CKS> = whiteProcedures

    open fun <ReqT, RespT> interceptCallInternal(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): Context = let {
        logger.debug { "outer interceptCallInternal; metadata=$headers" }
        Context.current().addClaims(headers)
    }

    override fun initialize() = super.initialize().also { logger.debug { "gRPC white procedures: ${getWhiteProcedures()}" } }

    private fun Context.addClaims(headers: Metadata): Context = this.withValue(CK_JWT_CLAIMS, headers.getJwtClaims())

    private fun Metadata.getJwtClaims(): Claims = get(GRPC_BEARER_AUTHORIZATION)
        ?.run {
            jwtSecurityService.getAllClaimsFromJwt(getJwtFromBearer())
        } ?: error(findI18nMessage(JWT_IS_MISSING))
}
