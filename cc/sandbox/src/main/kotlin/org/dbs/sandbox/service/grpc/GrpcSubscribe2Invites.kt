package org.dbs.sandbox.service.grpc

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.grpc.consts.FlowResponse
import org.dbs.protobuf.core.MainResponse as RESP
import org.dbs.sandbox.invite.client.CreatedInviteDto as ENT
import org.dbs.sandbox.invite.client.InviteSubscribeRequest as REQ

object GrpcSubscribe2Invites {

    fun SandBoxGrpcService.subscribe2Invite(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): FlowResponse = request.run dto@{

        runBlocking { validateRemoteAddress(remoteAddress) }

        logger.info {"receive request: $request"}

        flow {
            repeat (3) {
                emit(row(it.toString()))
                delay(10)
            }
            logger.info {"finish request: $request"}
        }
    }

    private suspend fun SandBoxGrpcService.row(ic: String): RESP = buildGrpcResponseOld {
            it.run {
                val entityBuilder by lazy { ENT.newBuilder() }
                entityBuilder.setInviteCode(ic)
                entityBuilder
            }
    }
}
