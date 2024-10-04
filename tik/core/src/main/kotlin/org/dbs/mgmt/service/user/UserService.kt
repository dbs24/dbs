package org.dbs.mgmt.service.user

import org.dbs.consts.Email
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.mgmt.client.CreateOrUpdateUserRequest
import org.dbs.mgmt.model.user.User
import org.dbs.spring.core.api.ServiceBean
import org.dbs.customer.UserLogin
import org.dbs.customer.UserPassword
import reactor.core.publisher.Mono

interface UserService: ServiceBean {
    suspend fun getOrCreateDefaultRootUser(): Mono<User>
    suspend fun saveHistory(entity: User): User
    suspend fun createNewUser(userLogin: UserLogin): User
    suspend fun findUserByLogin(userLogin: UserLogin): User?
    suspend fun findUserByEmail(userEmail: Email): User?
    fun setUserNewStatus(user: User, status: EntityStatusEnum): User
    fun setUserNewPassword(user: User, password: UserPassword): User
    fun updateUser(src: User, srcDto: CreateOrUpdateUserRequest): User
}