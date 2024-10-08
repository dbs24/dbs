package org.dbs.goods.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.goods.client.*
import org.dbs.goods.service.ApplicationServiceGate
import org.dbs.goods.service.grpc.GrpcCreateOrUpdateUser.createOrUpdateUserInternal
import org.dbs.goods.service.grpc.GrpcGetUserCredentials.getUserCredentialsInternal
import org.dbs.goods.service.grpc.GrpcUpdateUserPassword.updateUserPasswordInternal
import org.dbs.goods.service.grpc.GrpcUpdateUserStatus.updateUserStatusInternal
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.stereotype.Service

@Service
class GoodsGrpcService : AbstractGrpcServerService(), PublicApplicationBean, ApplicationServiceGate {

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
