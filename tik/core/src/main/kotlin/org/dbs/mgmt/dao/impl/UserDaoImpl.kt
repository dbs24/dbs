package org.dbs.mgmt.dao.impl

import org.dbs.consts.Email
import org.dbs.consts.Login
import org.dbs.mgmt.dao.UserDao
import org.dbs.mgmt.repo.user.UserRepo
import org.dbs.customer.UserCore.CacheKeyUserEnum.CC_USER_ID
import org.dbs.customer.UserCore.CacheKeyUserEnum.CC_USER_LOGIN
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import org.dbs.mgmt.model.user.User as ENTITY

@Service
class UserDaoImpl(
    val userRepo: UserRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService(), UserDao {

    override suspend fun findUserByLoginCo(login: Login): ENTITY? =
        entityCacheService.getEntity(CC_USER_LOGIN, login) {
            userRepo.findByLogin(login)
        }

    override suspend fun findUserByEmailCo(email: Email): ENTITY? = userRepo.findByEmail(email)

    override fun invalidateCaches(playerLogin: Login)  {
            entityCacheService.invalidateCaches(playerLogin, CC_USER_LOGIN)
            entityIdCacheService.invalidateCaches(playerLogin, CC_USER_ID)
        }
}
