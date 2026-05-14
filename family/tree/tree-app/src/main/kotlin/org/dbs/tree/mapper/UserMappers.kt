package org.dbs.tree.mapper

import org.dbs.application.core.service.funcs.IntFuncs.toLocalDate
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.tree.client.CreateOrUpdateUserRequest
import org.dbs.tree.client.CreateOrUpdateUserResponse
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand
import org.dbs.tree.model.user.User
import org.dbs.tree.service.UserService
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime.now

typealias Dto2User = (src: User, srcDto: CreateOrUpdateUserRequest, passwordEncoder: PasswordEncoder) -> User

object UserMappers {

    fun CreateOrUpdateUserRequest.toCommand(): CreateOrUpdateUserCommand =
        CreateOrUpdateUserCommand(
            oldLogin = oldLogin.grpcGetOrNull(),
            login = login,
            oldEmail = oldEmail.grpcGetOrNull(),
            email = email,
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
            oldLogin = oldLogin,
            login = login,
            oldEmail = oldEmail,
            email = email,
            password = password,
            firstName =  firstName,
            middleName = firstName,
            lastName = lastName,
            phone = phone
        )

    val dto2User: Dto2User = { src, dto, passwordEncoder ->
        src.copy(
            login = dto.login.grpcGetOrNull() ?: dto.oldLogin,
            email = dto.email.grpcGetOrNull() ?: dto.oldEmail,
            lastName = dto.lastName.grpcGetOrNull(),
            middleName = dto.middleName.grpcGetOrNull(),
            firstName = dto.firstName.grpcGetOrNull(),
            birthDate = dto.birthDate.toLocalDate(),
            phone = dto.phone.grpcGetOrNull(),
            password = dto.password.grpcGetOrNull()?.let { passwordEncoder.encode(it) } ?: src.password,
            modifyDate = now(),
        )
    }

    fun UserService.updateUser(src: User, srcDto: CreateOrUpdateUserRequest): User =
        dto2User(src, srcDto, passwordEncoder)
}
