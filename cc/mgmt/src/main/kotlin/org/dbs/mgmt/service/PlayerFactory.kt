package org.dbs.mgmt.service

import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.mgmt.model.hist.PlayerHist
import org.dbs.mgmt.model.player.Player
import org.dbs.player.PlayerCore.EntityStatus.ES_PLAYER_ACTUAL
import org.dbs.player.PlayerCore.EntityStatus.ES_PLAYER_ANONYMOUS
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import org.dbs.mgmt.model.player.Player as ENTITY


@Service
class PlayerFactory(
    val passwordEncoder: PasswordEncoder
) : AbstractApplicationService() {

    fun createRootPlayer(): ENTITY = ENTITY(
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
        entityStatus = ES_PLAYER_ACTUAL,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun createNewPlayer(): ENTITY = ENTITY(
        login = "",
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
        entityStatus = ES_PLAYER_ANONYMOUS,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun createHist(src: Player): PlayerHist = PlayerHist(
        actualDate = src.modifyDate,
        playerId = requireNotNull(src.playerId) { "Player must be persisted before creating history" },
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
        password = src.password,
        entityStatus = src.entityStatus,
        createDate = src.createDate,
        modifyDate = src.modifyDate,
        closeDate = src.closeDate,
    )
}
