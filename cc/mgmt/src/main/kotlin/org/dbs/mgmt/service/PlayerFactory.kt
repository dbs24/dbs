package org.dbs.mgmt.service


import org.dbs.consts.EntityId
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.mgmt.model.hist.PlayerHist
import org.dbs.mgmt.model.player.Player
import org.dbs.player.PlayerCore.EntityStatus.ES_PLAYER_ACTUAL
import org.dbs.player.PlayerCore.EntityStatus.ES_PLAYER_ANONYMOUS
import org.dbs.service.v2.EntityCoreValExt.asNew
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.dbs.mgmt.model.player.Player as ENTITY


@Service
class PlayerFactory (
    val passwordEncoder: PasswordEncoder
): AbstractApplicationService() {
    fun createRootPlayer(id : EntityId) : ENTITY = ENTITY(
        playerId = id,
        login = ROOT_USER,
        firstName = ROOT_USER,
        middleName = ROOT_USER,
        lastName = ROOT_USER,
        email = null,
        phone = null,
        password = passwordEncoder.encode(ROOT_USER_PASS),
        country = null,
        avatar = null,
        smallAvatar = null,
        gender = null,
        birthDate = null,
    ).asNew(ES_PLAYER_ACTUAL)

    fun createNewPlayer(id: EntityId) : ENTITY =
        ENTITY(
            playerId = id,
            login = id.hashCode().toString(),
            firstName = null,
            middleName = null,
            lastName = null,
            email = null,
            phone = null,
            password = null,
            country = null,
            avatar = null,
            smallAvatar = null,
            gender = null,
            birthDate = null,
        ).asNew(ES_PLAYER_ANONYMOUS)

    fun createHist(src: Player) =
        PlayerHist(
            actualDate = src.entityCore.modifyDate,
            playerId = src.playerId,
            login = src.login,
            email = src.email,
            phone = src.phone,
            gender = src.gender,
            lastName = src.lastName,
            middleName = src.middleName,
            firstName = src.firstName,
            birthDate = src.birthDate,
            country = src.country,
            avatar = src.avatar,
            smallAvatar = src.smallAvatar,
            password = src.password
        )
}
