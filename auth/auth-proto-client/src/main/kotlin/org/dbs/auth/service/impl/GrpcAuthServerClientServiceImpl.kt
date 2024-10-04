package org.dbs.auth.service.impl

import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.service.GrpcAuthServerClientService
import org.dbs.consts.Jwt
import org.dbs.consts.SpringCoreConst.PropertiesNames.AUTH_SERVER_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.AUTH_SERVER_SERVICE_PORT
import org.dbs.protobuf.auth.AuthServerClientServiceGrpcKt
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.protobuf.auth.FindJwtRequest
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcAuthServerClientServiceImpl(
    @Value("\${$AUTH_SERVER_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$AUTH_SERVER_SERVICE_PORT:6453}")
    private val grpcPort: Int
) : GrpcAuthServerClientService,
    AbstractGrpcClientService<AuthServerClientServiceGrpcKt.AuthServerClientServiceCoroutineStub>(grpcUrl, grpcPort) {
    override fun introspectJwt(jwt: Jwt): MainResponse = grpcCall {
        logger.debug { "try 2 verify jwt: '${jwt.last15()}'" }
        it.findJwt(
            FindJwtRequest.newBuilder()
                .setJwt(jwt)
                .build()
        ).also {
            logger.debug { "answer: ${it.responseAnswer}" }
        }
    }
}

