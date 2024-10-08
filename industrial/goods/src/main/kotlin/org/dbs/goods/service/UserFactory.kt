package org.dbs.goods.service


import org.dbs.consts.EntityId
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.goods.model.hist.UserHist
import org.dbs.goods.model.user.User
import org.dbs.goods.UserCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.service.v2.EntityCoreValExt.asNew
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.dbs.goods.model.user.User as ENTITY


@Service
class UserFactory (
    val passwordEncoder: PasswordEncoder
): AbstractApplicationService() {
    fun createRootUser(id : EntityId) : ENTITY = ENTITY(
        userId = id,
        login = ROOT_USER,
        firstName = ROOT_USER,
        lastName = ROOT_USER,
        email = null,
        password = passwordEncoder.encode(ROOT_USER_PASS),
    ).asNew(ES_USER_ACTUAL)

    fun createNewUser(id: EntityId) : ENTITY =
        ENTITY(
            userId = id,
            login = id.hashCode().toString(),
            firstName = null,
            lastName = null,
            email = null,
            password = null,
        ).asNew(ES_USER_ACTUAL)

    fun createHist(src: User) =
        UserHist(
            actualDate = src.entityCore.modifyDate,
            userId = src.userId,
            login = src.login,
            email = src.email,
            lastName = src.lastName,
            firstName = src.firstName,
            password = src.password
        )
}
