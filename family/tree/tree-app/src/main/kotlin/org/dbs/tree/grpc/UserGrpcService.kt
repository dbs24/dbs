package org.dbs.tree.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.tree.client.CreateOrUpdateUserRequest
import org.dbs.tree.client.CreateOrUpdateUserResponse
import org.dbs.tree.client.UpdateUserPasswordRequest
import org.dbs.tree.client.UpdateUserPasswordResponse
import org.dbs.tree.client.UpdateUserStatusRequest
import org.dbs.tree.client.UpdateUserStatusResponse
import org.dbs.tree.client.UserCredentialsRequest
import org.dbs.tree.client.UserCredentialsResponse
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.mapper.UserMappers.toCommand
import org.dbs.tree.mapper.UserMappers.toUserProto
import org.dbs.tree.service.UserService

@GrpcService
class GrpcUserController(val userService: UserService) : UserServiceGrpcKt.UserServiceCoroutineImplBase() {

        override suspend fun createOrUpdateUser(request: CreateOrUpdateUserRequest): CreateOrUpdateUserResponse =
                userService.createOrUpdateUser(request.toCommand()).toUserProto()

        override suspend fun getUserCredentials(request: UserCredentialsRequest): UserCredentialsResponse =
                TODO()

            // getUserCredentialsInternal(request)
        override suspend fun updateUserStatus(request: UpdateUserStatusRequest): UpdateUserStatusResponse =
                TODO()
        // updateUserStatusInternal(request)

        override suspend fun updateUserPassword(request: UpdateUserPasswordRequest): UpdateUserPasswordResponse =
                TODO()
        // updateUserPasswordInternal(request)

}
