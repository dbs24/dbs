package org.dbs.ext


object JwtGrpcExt {
/*
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

 */
}
