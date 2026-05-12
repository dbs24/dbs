package org.dbs.tree.service

import kotlinx.coroutines.runBlocking
import org.dbs.consts.Email
import org.dbs.consts.StringNote
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.model.LogEntityAction
import org.dbs.rest.validation.ValidateDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.user.UserCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.UserCore.isClosedUser
import org.dbs.user.UserLogin
import org.dbs.user.UserPassword
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime.now
import org.dbs.tree.dao.UserDao as DAO
import org.dbs.tree.model.user.User as ENTITY

@Service
@Lazy(false)
@DependsOn("r2dbcPersistenceService")
class UserService(
    val dao: DAO,
    val passwordEncoder: PasswordEncoder,
    val userFactory: UserFactory,
) : AbstractApplicationService() {


    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        runBlocking { findUserByLogin(ROOT_USER) ?: dao.saveUser(userFactory.createRootUser()) }
    }

    @ValidateDto
    @LogEntityAction("EA_CREATE_OR_UPDATE_USER")
    @Transactional
    suspend fun createOrUpdateUser(request: CreateOrUpdateUserDto): ENTITY {

        val updatedUser = dao.findUserByLogin(request.login) ?: createNewUser(request.login)
        val isNewUser = request.oldLogin == null

        return dao.saveUser(updatedUser.copy(
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            password = passwordEncoder.encode(request.password),
            entityStatus = ES_USER_ANONYMOUS.takeIf { isNewUser } ?: updatedUser.status,
            modifyDate = if (!isNewUser) now() else updatedUser.modifyDate,
            closeDate = updatedUser.closeDate
        ))

    }

    suspend fun createNewUser(userLogin: UserLogin): ENTITY =
        userFactory.createNewUser(userLogin).also {
            logger.debug { "create new user login: $userLogin" }
        }

    suspend fun findUserByLogin(userLogin: UserLogin): ENTITY? =
        dao.findUserByLogin(userLogin.also { logger.debug { "find user login: $userLogin" } })

    suspend fun findUserByEmail(userEmail: Email): ENTITY? =
        dao.findUserByEmail(userEmail)

    fun setUserNewStatus(user: ENTITY, status: EntityStatusEnum): ENTITY =
        user.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedUser(status)) now() else null,
        ).also {
            dao.invalidateCaches(user.login)
        }

    fun setUserNewPassword(user: ENTITY, password: UserPassword): ENTITY =
        user.copy(password = passwordEncoder.encode(password), modifyDate = now())
            .also {
                dao.invalidateCaches(user.login)
            }

    fun updateUser(src: ENTITY, actionNote: StringNote): ENTITY =
        src.copy(modifyDate = now())
}
