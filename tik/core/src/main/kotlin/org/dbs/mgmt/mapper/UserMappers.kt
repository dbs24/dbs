package org.dbs.mgmt.mapper

import org.dbs.application.core.service.funcs.IntFuncs.toLocalDate
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.mgmt.client.CreateOrUpdateUserRequest
import org.dbs.mgmt.model.user.User
import org.dbs.mgmt.service.impl.UserServiceImpl
import org.dbs.service.v2.EntityCoreValExt.copyEntity
import org.springframework.security.crypto.password.PasswordEncoder

typealias Dto2User = (src: User, srcDto: CreateOrUpdateUserRequest, passwordEncoder: PasswordEncoder) -> User

object UserMappers {

    val dto2User: Dto2User = { src, dto, passwordEncoder ->
        src.copyEntity {
            src.copy(
                login = dto.login.grpcGetOrNull() ?: dto.oldLogin,
                email = dto.email.grpcGetOrNull() ?: dto.oldEmail,
                lastName = dto.lastName.grpcGetOrNull(),
                middleName = dto.middleName.grpcGetOrNull(),
                firstName = dto.firstName.grpcGetOrNull(),
                birthDate = dto.birthDate.toLocalDate(),
                phone = dto.phone.grpcGetOrNull(),
                password = dto.password.grpcGetOrNull()?.let { passwordEncoder.encode(it) },
                country = dto.country.grpcGetOrNull(),
                gender = dto.gender.grpcGetOrNull(),
                avatar = dto.avatar.grpcGetOrNull(),
                smallAvatar = dto.smallAvatar.grpcGetOrNull()
            )
        }
    }

    fun UserServiceImpl.updateUser(src: User, srcDto: CreateOrUpdateUserRequest): User =
        dto2User(src, srcDto, passwordEncoder)

}
