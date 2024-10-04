package org.dbs.mgmt.dao

import org.dbs.consts.Email
import org.dbs.consts.Login
import org.dbs.mgmt.model.user.User

interface UserDao {
    suspend fun findUserByLoginCo(login: Login): User?
    suspend fun findUserByEmailCo(email: Email): User?
    fun invalidateCaches(playerLogin: Login)
}