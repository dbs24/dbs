package org.dbs.goods.mapper

import org.dbs.application.core.service.funcs.IntFuncs.toLocalDate
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.goods.client.CreateOrUpdateUserRequest
import org.dbs.goods.model.user.User
import org.dbs.goods.service.UserService
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
                firstName = dto.firstName.grpcGetOrNull(),
                password = dto.password.grpcGetOrNull()?.let { passwordEncoder.encode(it) },
            )
        }
    }

    fun UserService.updateUser(src: User, srcDto: CreateOrUpdateUserRequest): User =
        dto2User(src, srcDto, passwordEncoder)

}
