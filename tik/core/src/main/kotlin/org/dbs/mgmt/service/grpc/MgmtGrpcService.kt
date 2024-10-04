package org.dbs.mgmt.service.grpc

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.mgmt.client.*
import org.dbs.mgmt.service.ApplicationServiceGate
import org.dbs.mgmt.service.grpc.GrpcCreateOrUpdateUser.createOrUpdateUserInternal
import org.dbs.mgmt.service.grpc.GrpcUpdateUserStatus.updateUserStatusInternal
import org.dbs.mgmt.service.grpc.GrpcGetUserCredentials.getUserCredentialsInternal
import org.dbs.mgmt.service.grpc.GrpcUpdateUserPassword.updateUserPasswordInternal
import org.dbs.customer.UserLogin
import org.dbs.customer.UsersConsts.Claims.CL_USER_LOGIN
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.stereotype.Service

@Service
class MgmtGrpcService : AbstractGrpcServerService(), PublicApplicationBean, ApplicationServiceGate {

    override fun <ReqT, RespT> interceptCallInternal(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): Context = call.run {

        Context.current()
            .addUserLogin(call, headers)
            .also { logger.debug { "currentContext: $it" } }
    }

    fun <ReqT, RespT> Context.addUserLogin(call: ServerCall<ReqT, RespT>, headers: Metadata): Context =
//        if (proceduresRequiresUserLogin.isContainProcedure(call.getProcedureName())) {
//            this.withValue(CK_USER_LOGIN, headers.getUserLogin())
//        } else
        this

    fun Metadata.getUserLogin(): UserLogin = getJwtClaim(CL_USER_LOGIN)

    @GrpcService
    inner class AnalystService : UserServiceGrpcKt.UserServiceCoroutineImplBase(),
        PublicApplicationBean {
        override suspend fun getUserCredentials(request: UserCredentialsRequest) =
            getUserCredentialsInternal(request)

        override suspend fun createOrUpdateUser(request: CreateOrUpdateUserRequest) =
            createOrUpdateUserInternal(request)

        override suspend fun updateUserStatus(request: UpdateUserStatusRequest) =
            updateUserStatusInternal(request)

        override suspend fun updateUserPassword(request: UpdateUserPasswordRequest) =
            updateUserPasswordInternal(request)

    }
}
