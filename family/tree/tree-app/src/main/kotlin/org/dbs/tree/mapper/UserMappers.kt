package org.dbs.tree.mapper

import org.dbs.application.value.Email
import org.dbs.application.value.UserLogin
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.tree.client.CreateOrUpdateUserRequest
import org.dbs.tree.client.CreateOrUpdateUserResponse
import org.dbs.tree.client.UserCredentialsRequest
import org.dbs.tree.client.UserCredentialsResponse
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand
import org.dbs.tree.model.domain.GetUserCredentialsCommand
import org.dbs.tree.model.user.User
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.springframework.security.crypto.password.PasswordEncoder

typealias Dto2User = (src: User, srcDto: CreateOrUpdateUserRequest, passwordEncoder: PasswordEncoder) -> User

object UserMappers {

    fun CreateOrUpdateUserRequest.toCommand(): CreateOrUpdateUserCommand =
        CreateOrUpdateUserCommand(
            oldLogin = oldLogin.grpcGetOrNull()?.let { UserLogin(it) },
            login = UserLogin(login),
            oldEmail = oldEmail.grpcGetOrNull()?.let { Email(it) },
            email = Email(email),
            password = password,
            firstName =  firstName.grpcGetOrNull(),
            middleName = firstName.grpcGetOrNull(),
            lastName = lastName.grpcGetOrNull(),
            phone = phone.grpcGetOrNull()
        )

    fun User.toUserProto() : CreateOrUpdateUserResponse = CreateOrUpdateUserResponse.newBuilder()
        .also {
            it.email = email
            it.userLogin = login
            it.status = status.entityStatusName
        }.build()

    fun User.toUserDto() : CreatedUserDto = CreatedUserDto(
            email = email,
            modifiedLogin = login,
            status = status.entityStatusName
    )

    fun CreateOrUpdateUserDto.toCommand() : CreateOrUpdateUserCommand =
        CreateOrUpdateUserCommand(
            oldLogin = oldLogin?.let { UserLogin(it) },
            login = UserLogin(login),
            oldEmail = oldEmail?.let { Email(it) },
            email = Email(email),
            password = password,
            firstName =  firstName,
            middleName = firstName,
            lastName = lastName,
            phone = phone
        )

    fun UserCredentialsRequest.toCommand(): GetUserCredentialsCommand =
        GetUserCredentialsCommand(
            login = UserLogin(userLogin)
        )

    fun User.toUserCredentialsProto(): UserCredentialsResponse = UserCredentialsResponse.newBuilder()
        .also {
            it.userLogin = login
            it.userPassword = password
        }.build()
}
