package org.dbs.goods.service

import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.goods.UserCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.goods.UserLogin
import org.dbs.goods.model.hist.UserHist
import org.dbs.goods.model.user.User
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import org.dbs.goods.model.user.User as ENTITY

@Service
class UserFactory(
    val passwordEncoder: PasswordEncoder
) : AbstractApplicationService() {

    fun createRootUser(): ENTITY = ENTITY(
        login = ROOT_USER,
        firstName = ROOT_USER,
        lastName = ROOT_USER,
        email = null,
        password = passwordEncoder.encode(ROOT_USER_PASS),
        entityStatus = ES_USER_ACTUAL,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun createNewUser(userLogin: UserLogin): ENTITY = ENTITY(
        login = userLogin,
        firstName = null,
        lastName = null,
        email = null,
        password = null,
        entityStatus = ES_USER_ACTUAL,
        createDate = now(),
        modifyDate = now(),
        closeDate = null,
    )

    fun createHist(src: User): UserHist = UserHist(
        actualDate = src.modifyDate,
        userId = src.userId!!,
        login = src.login,
        email = src.email,
        lastName = src.lastName,
        firstName = src.firstName,
        password = src.password,
        entityStatus = src.entityStatus,
        createDate = src.createDate,
        modifyDate = src.modifyDate,
        closeDate = src.closeDate,
    )
}
