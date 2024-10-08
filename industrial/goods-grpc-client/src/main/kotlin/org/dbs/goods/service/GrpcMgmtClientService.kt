package org.dbs.goods.service

import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_NONE
import org.dbs.consts.SpringCoreConst.PropertiesNames.USER_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.USER_SERVICE_PORT
import org.dbs.goods.client.UserCredentialsRequest
import org.dbs.goods.client.UserServiceGrpcKt
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcMgmtClientService(
    @Value("\${$USER_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$USER_SERVICE_PORT:$URI_LOCALHOST_NONE}")
    private val grpcPort: Int
) : MgmtInterfaces, AbstractGrpcClientService<UserServiceGrpcKt.UserServiceCoroutineStub>(grpcUrl, grpcPort) {
    override fun getUserCredentials(
        userLogin: String,
    ): MainResponse = grpcCall {
        it.getUserCredentials(
            UserCredentialsRequest.newBuilder()
                .setUserLogin(userLogin)
                .also {
                    logger.debug {
                        "receive userCredentials request: '$userLogin"
                    }
                }.build()).also { logger.debug { "receive managerCredentials answer: ${it.responseAnswer}" }
            }
    }
}
