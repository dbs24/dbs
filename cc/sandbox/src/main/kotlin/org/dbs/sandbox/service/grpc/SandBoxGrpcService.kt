package org.dbs.sandbox.service.grpc

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.consts.EntityCode
import org.dbs.grpc.consts.FlowResponse
import org.dbs.invite.InviteConsts.Claims.CL_INVITE_CODE
import org.dbs.sandbox.invite.client.CreateOrUpdateInviteRequest
import org.dbs.sandbox.invite.client.InviteServiceGrpcKt
import org.dbs.sandbox.invite.client.InviteSubscribeRequest
import org.dbs.sandbox.invite.client.UpdateInviteStatusRequest
import org.dbs.sandbox.service.ApplicationServiceGate
import org.dbs.sandbox.service.grpc.GrpcCreateOrUpdateInvite.createOrUpdateInviteInternal
import org.dbs.sandbox.service.grpc.GrpcSubscribe2Invites.subscribe2Invite
import org.dbs.sandbox.service.grpc.GrpcUpdateInviteStatus.updateInviteStatusInternal
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.stereotype.Service

@Service
class SandBoxGrpcService : AbstractGrpcServerService(), PublicApplicationBean, ApplicationServiceGate {

    override fun <ReqT, RespT> interceptCallInternal(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): Context = call.run {

        Context.current()
            .addInviteLogin(call, headers)
            .also { logger.debug { "currentContext: $it" } }
    }

    fun <ReqT, RespT> Context.addInviteLogin(call: ServerCall<ReqT, RespT>, headers: Metadata): Context =
//        if (proceduresRequiresInviteLogin.isContainProcedure(call.getProcedureName())) {
//            this.withValue(CK_PLAYER_LOGIN, headers.getInviteLogin())
//        } else
        this

    fun Metadata.getInviteLogin(): EntityCode = getJwtClaim(CL_INVITE_CODE)

    @GrpcService
    inner class SandBoxService : InviteServiceGrpcKt.InviteServiceCoroutineImplBase(), PublicApplicationBean {

        override suspend fun createOrUpdateInvite(request: CreateOrUpdateInviteRequest) =
            createOrUpdateInviteInternal(request)

        override suspend fun updateInviteStatus(request: UpdateInviteStatusRequest) =
            updateInviteStatusInternal(request)

        override fun subscribe2Invites(request: InviteSubscribeRequest)
        : FlowResponse = subscribe2Invite(request)

    }
}
