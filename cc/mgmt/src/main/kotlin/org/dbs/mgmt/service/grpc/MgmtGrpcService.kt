package org.dbs.mgmt.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.mgmt.client.*
import org.dbs.mgmt.service.ApplicationServiceGate
import org.dbs.mgmt.service.grpc.GrpcCreateOrUpdatePlayer.createOrUpdatePlayerInternal
import org.dbs.mgmt.service.grpc.GrpcGetPlayerCredentials.getPlayerCredentialsInternal
import org.dbs.mgmt.service.grpc.GrpcUpdatePlayerPassword.updatePlayerPasswordInternal
import org.dbs.mgmt.service.grpc.GrpcUpdatePlayerStatus.updatePlayerStatusInternal
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.stereotype.Service

@Service
class MgmtGrpcService : AbstractGrpcServerService(), PublicApplicationBean, ApplicationServiceGate {

    @GrpcService
    inner class AnalystService : PlayerServiceGrpcKt.PlayerServiceCoroutineImplBase(),
        PublicApplicationBean {
        override suspend fun getPlayerCredentials(request: PlayerCredentialsRequest) =
            getPlayerCredentialsInternal(request)

        override suspend fun createOrUpdatePlayer(request: CreateOrUpdatePlayerRequest) =
            createOrUpdatePlayerInternal(request)

        override suspend fun updatePlayerStatus(request: UpdatePlayerStatusRequest) =
            updatePlayerStatusInternal(request)

        override suspend fun updatePlayerPassword(request: UpdatePlayerPasswordRequest) =
            updatePlayerPasswordInternal(request)

    }
}
