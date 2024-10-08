package org.dbs.goods.dao

import org.dbs.consts.Email
import org.dbs.consts.Login
import org.dbs.goods.repo.user.UserRepo
import org.dbs.goods.UserCore.CacheKeyUserEnum.CC_USER_ID
import org.dbs.goods.UserCore.CacheKeyUserEnum.CC_USER_LOGIN
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import org.dbs.goods.model.user.User as ENTITY

@Service
class UserDao(
    val userRepo: UserRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService() {

    suspend fun findUserByLogin(login: Login): ENTITY? =
        entityCacheService.getEntity(CC_USER_LOGIN, login) {
            userRepo.findByLogin(login)
        }

    suspend fun findUserByEmailCo(email: Email): ENTITY? = userRepo.findByEmail(email)

    fun invalidateCaches(userLogin: Login)  {
            entityCacheService.invalidateCaches(userLogin, CC_USER_LOGIN)
            entityIdCacheService.invalidateCaches(userLogin, CC_USER_ID)
        }
}
