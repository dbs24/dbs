package org.dbs.tree.service

import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.user.UserCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.user.UserCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.UserLogin
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import org.dbs.tree.model.user.User as ENTITY


@Service
class UserFactory(
    val passwordEncoder: PasswordEncoder
) : AbstractApplicationService() {

    fun createRootUser(): ENTITY = ENTITY(
        login = ROOT_USER,
        firstName = ROOT_USER,
        middleName = ROOT_USER,
        lastName = ROOT_USER,
        email = null,
        phone = null,
        password = passwordEncoder.encode(ROOT_USER_PASS),
        birthDate = null,
        entityStatus = ES_USER_ACTUAL,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun createNewUser(login: UserLogin): ENTITY = ENTITY(
        login = login,
        firstName = null,
        middleName = null,
        lastName = null,
        email = null,
        phone = null,
        password = null,
        birthDate = null,
        entityStatus = ES_USER_ANONYMOUS,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

}
