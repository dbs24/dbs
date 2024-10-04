package org.dbs.actor.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.CM_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.CM_SERVICE_PORT
import org.dbs.cm.client.AnalystClientServiceGrpcKt
import org.dbs.cm.client.PlayerCredentialsRequest
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcPlayerClientService(
    @Value("\${$CM_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$CM_SERVICE_PORT:6453}")
    private val grpcPort: Int
) : AbstractGrpcClientService<AnalystClientServiceGrpcKt.AnalystClientServiceCoroutineStub>(grpcUrl, grpcPort) {
    suspend fun getPlayerCredentials(
        playerLogin: String,
    ) = grpcCall {
        it.getPlayerCredentials(
            PlayerCredentialsRequest.newBuilder()
                .setPlayerLogin(playerLogin)
                .also {
                    logger.debug {
                        "receive playerCredentials request: '$playerLogin"
                    }
                }.build()).also { logger.debug { "build playerCredentials answer: ${it.responseAnswer}" }
            }
    }
}
