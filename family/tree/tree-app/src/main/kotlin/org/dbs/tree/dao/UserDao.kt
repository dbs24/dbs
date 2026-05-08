package org.dbs.tree.dao

import org.dbs.consts.Email
import org.dbs.consts.Login
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.UserCore.CacheKeyUserEnum.CC_USER_ID
import org.dbs.user.UserCore.CacheKeyUserEnum.CC_USER_LOGIN
import org.springframework.stereotype.Service
import org.dbs.tree.model.user.User as ENTITY

@Service
class UserDao(
    val userRepo: UserRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService() {

    suspend fun createUser(user: User) : User = userRepo.save(user)

    suspend fun findUserByLogin(login: Login): ENTITY? =
        entityCacheService.getEntity(CC_USER_LOGIN, login) {
            userRepo.findByLogin(login)
        }

    suspend fun findUserByEmail(email: Email): ENTITY? = userRepo.findByEmail(email)

    fun invalidateCaches(userLogin: Login)  {
            entityCacheService.invalidateCaches(userLogin, CC_USER_LOGIN)
            entityIdCacheService.invalidateCaches(userLogin, CC_USER_ID)
        }
}
