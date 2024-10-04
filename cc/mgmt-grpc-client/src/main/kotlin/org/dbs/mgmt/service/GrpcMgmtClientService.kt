package org.dbs.mgmt.service

import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_NONE
import org.dbs.consts.SpringCoreConst.PropertiesNames.MGMT_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.MGMT_SERVICE_PORT
import org.dbs.mgmt.client.PlayerCredentialsRequest
import org.dbs.mgmt.client.PlayerServiceGrpcKt
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcMgmtClientService(
    @Value("\${$MGMT_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$MGMT_SERVICE_PORT:$URI_LOCALHOST_NONE}")
    private val grpcPort: Int
) : MgmtInterfaces, AbstractGrpcClientService<PlayerServiceGrpcKt.PlayerServiceCoroutineStub>(grpcUrl, grpcPort) {
    override fun getPlayerCredentials(
        playerLogin: String,
    ): MainResponse = grpcCall {
        it.getPlayerCredentials(
            PlayerCredentialsRequest.newBuilder()
                .setPlayerLogin(playerLogin)
                .also {
                    logger.debug {
                        "receive playerCredentials request: '$playerLogin"
                    }
                }.build()).also { logger.debug { "receive managerCredentials answer: ${it.responseAnswer}" }
            }
    }
}
