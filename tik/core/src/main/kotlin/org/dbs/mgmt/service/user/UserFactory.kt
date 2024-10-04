package org.dbs.mgmt.service.user


import org.dbs.consts.EntityId
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.mgmt.model.hist.UserHist
import org.dbs.mgmt.model.user.User
import org.dbs.customer.UserCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.service.v2.EntityCoreValExt.asNew
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.dbs.mgmt.model.user.User as ENTITY


@Service
class UserFactory (
    val passwordEncoder: PasswordEncoder
): AbstractApplicationService() {
    fun createRootUser(id : EntityId) : ENTITY = ENTITY(
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
    ).asNew(ES_USER_ACTUAL)

    fun createNewUser(id: EntityId) : ENTITY =
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
        ).asNew(ES_USER_ACTUAL)

    fun createHist(src: User) =
        UserHist(
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
