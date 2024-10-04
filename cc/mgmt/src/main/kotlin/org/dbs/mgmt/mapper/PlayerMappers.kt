package org.dbs.mgmt.mapper

import org.dbs.application.core.service.funcs.IntFuncs.toLocalDate
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.mgmt.client.CreateOrUpdatePlayerRequest
import org.dbs.mgmt.model.player.Player
import org.dbs.mgmt.service.PlayerService
import org.dbs.service.v2.EntityCoreValExt.copyEntity
import org.springframework.security.crypto.password.PasswordEncoder

typealias Dto2Player = (src: Player, srcDto: CreateOrUpdatePlayerRequest, passwordEncoder: PasswordEncoder) -> Player

object PlayerMappers {

    val dto2Player: Dto2Player = { src, dto, passwordEncoder ->
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

    fun PlayerService.updatePlayer(src: Player, srcDto: CreateOrUpdatePlayerRequest): Player =
        dto2Player(src, srcDto, passwordEncoder)

}
