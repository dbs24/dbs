package org.dbs.analyst.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.cm.client.CreateOrUpdatePlayerRequest
import org.dbs.analyst.model.player.Player
import org.dbs.application.core.service.funcs.TestFuncs.generateTestString20
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class PlayerMappers(
    private val objectMapper: ObjectMapper,
    private val passwordEncoder: PasswordEncoder
) : Logging {
//    fun updatePlayer(src: Player, srcDto: CreateOrUpdatePlayerDto): Player =
//        src.copy(
//            login = srcDto.login,
//            email = srcDto.email,
//            lastName = srcDto.lastName,
//            firstName = srcDto.firstName,
//            //status = srcDto.avatarImage?.let { objectMapper.writeValueAsString(it) },
//        )

    fun updatePlayer(src: Player, srcDto: CreateOrUpdatePlayerRequest): Player = run {
        val isUpdatePlayer = srcDto.oldLogin.isNotEmpty()
        src.copy(
            login = srcDto.login.grpcGetOrNull() ?: srcDto.oldLogin,
            email = srcDto.email.grpcGetOrNull() ?: srcDto.oldEmail,
            lastName = srcDto.lastName,
            firstName = srcDto.firstName,
            phone = srcDto.phone.grpcGetOrNull() ?: EMPTY_STRING,
            token = src.token.takeIf { isUpdatePlayer } ?: generateTestString20(),
            password = srcDto.password.grpcGetOrNull()?.let { passwordEncoder.encode(it) } ?: src.token,
            status = src.status
        ).let { it.takeIf { isUpdatePlayer } ?: it.asNew() }
    }
//    fun createHist(src: Player) =
//        PlayerHist(
//            actualDate = src.getCoreEntity().modifyDate,
//            playerId = src.playerId,
//            login = src.login,
//            email = src.email,
//            phone1 = src.phone1,
//            phone2 = src.phone2,
//            gender = src.gender,
//            lastName = src.lastName,
//            firstName = src.firstName,
//            birthDate = src.birthDate,
//            country = src.country,
//            region = src.region,
//            avatarImg = src.status
//        )

}
