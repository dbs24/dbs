package org.dbs.sandbox.service

import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_NONE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SANDBOX_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SANDBOX_SERVICE_PORT
import org.dbs.grpc.consts.FlowItemProcessor
import org.dbs.sandbox.invite.client.InviteServiceGrpcKt
import org.dbs.sandbox.invite.client.InviteSubscribeRequest
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcSandBoxClientService(
    @Value("\${$SANDBOX_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$SANDBOX_SERVICE_PORT:$URI_LOCALHOST_NONE}")
    private val grpcPort: Int
) : SandBoxInterfaces, AbstractGrpcClientService<InviteServiceGrpcKt.InviteServiceCoroutineStub>(grpcUrl, grpcPort) {
    override fun subscribe2Invites(flowItemProcessor: FlowItemProcessor) {
        addGrpcStreamJob {
            grpcFlowCall {
                it.subscribe2Invites(
                    InviteSubscribeRequest.newBuilder()
                        .setRating(0)
                        .setWhite(false)
                        .build()
                )
            }.collect(flowItemProcessor)
            logger.debug("finish request")
        }
    }
}
