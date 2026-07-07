package org.dbs.tree.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.model.LogEntityAction
import org.dbs.rest.validation.ValidateDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand
import org.dbs.tree.model.domain.GetUserCredentialsCommand
import org.dbs.tree.model.domain.UpdateUserPasswordCommand
import org.dbs.tree.model.domain.UpdateUserStatusCommand
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.EntityTypes.ET_USER
import org.dbs.user.FamilyTreeCore.isClosedUser
import org.dbs.user.UserLogin
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
        runBlocking(Dispatchers.IO) {
            findUserByLogin(ROOT_USER) ?: dao.saveUser(userFactory.createRootUser())
        }
    }

    @ValidateDto
    @LogEntityAction("EA_CREATE_OR_UPDATE_USER")
    @Transactional
    suspend fun createOrUpdateUser(request: CreateOrUpdateUserCommand): ENTITY {

        val updatedUser = if (request.isNewUser) createNewUser(request.login)
        else (request.updatedUser)

        return dao.saveUser(
            updatedUser.copy(
                email = request.email,
                firstName = request.firstName,
                lastName = request.lastName,
                password = passwordEncoder.encode(request.password),
                entityStatus = ES_USER_ANONYMOUS.takeIf { request.isNewUser } ?: updatedUser.status,
                modifyDate = if (!request.isNewUser) now() else updatedUser.modifyDate,
                closeDate = updatedUser.closeDate
            ))
    }

    @ValidateDto
    suspend fun getUserCredentials(request: GetUserCredentialsCommand): ENTITY {
        return request.updatedUser
    }

    @ValidateDto
    @LogEntityAction("EA_UPDATE_USER_STATUS")
    @Transactional
    suspend fun updateUserStatus(request: UpdateUserStatusCommand): ENTITY {

        val updatedUser = request.updatedUser
        val newStatus = EntityStatusEnum.findStatus<EntityStatus>(request.status, ET_USER)
            ?: error("EntityStatus not found: (${request.status})")
        val modifyDate = now()
        val closeDate = if (isClosedUser(newStatus)) modifyDate else null

        return dao.saveUser(
            updatedUser.copy(
                entityStatus = newStatus,
                modifyDate = modifyDate,
                closeDate = closeDate
            )
        )
    }

    @ValidateDto
    @LogEntityAction("EA_UPDATE_USER_PASSWORD")
    @Transactional
    suspend fun updateUserPassword(request: UpdateUserPasswordCommand): ENTITY {

        val updatedUser = request.updatedUser

        return dao.saveUser(
            updatedUser.copy(
                password = passwordEncoder.encode(request.newPassword)
            )
        )
    }

    private fun createNewUser(userLogin: UserLogin): ENTITY =
        userFactory.createNewUser(userLogin).also {
            logger.debug { "create new user login: $userLogin" }
        }

    suspend fun findUserByLogin(userLogin: UserLogin): ENTITY? =
        dao.findUserByLogin(userLogin.also { logger.debug { "find user login: $userLogin" } })

}
