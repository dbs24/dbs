package org.dbs.customer.service

import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_NONE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MGMT_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.MGMT_SERVICE_PORT
import org.dbs.mgmt.client.UserCredentialsRequest
import org.dbs.mgmt.client.UserServiceGrpcKt
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcCustomerClientService(
    @Value("\${$MGMT_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$MGMT_SERVICE_PORT:$URI_LOCALHOST_NONE}")
    private val grpcPort: Int
) : CustomerInterfaces, AbstractGrpcClientService<UserServiceGrpcKt.UserServiceCoroutineStub>(grpcUrl, grpcPort) {
    override fun getUserCredentials(
        playerLogin: String,
    ): MainResponse = grpcCall {
        it.getUserCredentials(
            UserCredentialsRequest.newBuilder()
                .setUserLogin(playerLogin)
                .also {
                    logger.debug {
                        "receive playerCredentials request: '$playerLogin"
                    }
                }.build()).also { logger.debug { "receive managerCredentials answer: ${it.responseAnswer}" }
            }
    }
}
