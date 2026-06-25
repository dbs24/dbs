package org.dbs.tree.dao

import org.dbs.consts.Login
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.UserRepo
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.dbs.tree.model.user.User as ENTITY

@Service
class UserDao(
    val userRepo: UserRepo,
) : DaoAbstractApplicationService() {

    @CacheEvict(value = ["users"], key = "#user.login")
    suspend fun saveUser(user: User) : User = userRepo.save(user)
    @Cacheable(value = ["users"], key = "#login", unless = "#result == null")
    suspend fun findUserByLogin(login: Login): ENTITY? = userRepo.findByLogin(login)

}
