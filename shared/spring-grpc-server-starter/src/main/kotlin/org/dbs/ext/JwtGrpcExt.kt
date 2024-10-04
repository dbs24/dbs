package org.dbs.ext

import io.grpc.Metadata
import io.grpc.Status
import io.grpc.Status.CANCELLED
import io.grpc.Status.OK
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_ABUSED_CONNECTION
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_BEARER_AUTHORIZATION
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_IS_AUTHORIZED
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_USER_AGENT
import org.dbs.consts.GrpcConsts.MetadataKeys.JWT_AUTHORIZATION_FAIL
import org.dbs.consts.Jwt
import org.dbs.consts.SecurityConsts.Claims.CL_USER_AGENT
import org.dbs.service.AbstractGrpcServerService

object JwtGrpcExt {

    fun AbstractGrpcServerService.processBearerJwt(
        metadata: Metadata,
        jwtStorage: LateInitVal<Jwt>,
        grpcProcedure: String): Status =
        metadata[GRPC_BEARER_AUTHORIZATION]
            ?.run {
                logger.debug { "authenticate grpc call with [${last15()}]" }
                jwtStorage.init(getJwtFromBearer())

                jwtValidator(jwtSecurityService, jwtStorage.value).let {
                    if (it != GRPC_IS_AUTHORIZED) {
                        logger.warn { this }
                        CANCELLED.withDescription(JWT_AUTHORIZATION_FAIL.name())
                    } else {

                        // validate UserAgent
                        metadata[GRPC_USER_AGENT]?.let {
                            jwtSecurityService.getClaim(jwtStorage.value, CL_USER_AGENT)?.apply {
                                if (it != this)
                                    logger.warn { "$grpcProcedure(${jwtStorage.value.last15()}): " +
                                            "invalid user agent applied ($it), should be '$this'" }
                            }
                        } ?: CANCELLED.withDescription(GRPC_ABUSED_CONNECTION.name())
                        OK
                    }
                }
            } ?: run {
            logger.warn { JWT_AUTHORIZATION_FAIL }
            CANCELLED.withDescription(JWT_AUTHORIZATION_FAIL.name())
        }
}
