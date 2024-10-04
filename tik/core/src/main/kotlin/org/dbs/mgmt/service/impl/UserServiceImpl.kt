package org.dbs.mgmt.service.impl

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.Email
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.mgmt.client.CreateOrUpdateUserRequest
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.r2dbcPersistenceService
import org.dbs.customer.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.customer.UserCore.isClosedUser
import org.dbs.customer.UserLogin
import org.dbs.customer.UserPassword
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.service.v2.EntityCoreValExt.updateStatus
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.mgmt.dao.UserDao
import org.dbs.mgmt.mapper.UserMappers.dto2User
import org.dbs.mgmt.model.user.User
import org.dbs.mgmt.service.user.UserFactory
import org.dbs.mgmt.service.user.UserService
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.mgmt.model.user.User as ENTITY

@Service
@Lazy(false)
class UserServiceImpl(
    val dao: UserDao,
    val passwordEncoder: PasswordEncoder,
    val userFactory: UserFactory,
) : AbstractApplicationService(), UserService {

    override fun initialize() = super.initialize().also {

        runBlocking {
            getOrCreateDefaultRootUser().subscribeMono()
        }
    }

    override suspend fun getOrCreateDefaultRootUser(): Mono<ENTITY> =  // crete Root role if not exists
        findUserByLogin(ROOT_USER).toMono()
            .flatMap { user ->
                user.run {
                    if (passwordEncoder.matches(ROOT_USER_PASS, password)) {
                        logger.warn("Please, replace default password for user '$ROOT_USER'".uppercase())
                    }
                    toMono()
                }
            }
            .switchIfEmpty { createRootUser }

    //------------------------------------------------------------------------------------------------------------------
    override suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
            if (!justCreated.value)
                r2dbcPersistenceService.saveEntityHistCo(userFactory.createHist(entity))
                    .let {
                        dao.invalidateCaches(entity.login)
                        entity
                    }
            else this
        }

    private val createRootUser: Mono<ENTITY> = runBlocking {
        generateNewEntityId().toMono()
            .map { userFactory.createRootUser(it) }
            .flatMap { executeAction(it, EA_CREATE_OR_UPDATE_USER) }
    }

    override suspend fun createNewUser(userLogin: UserLogin): ENTITY =
        generateNewEntityId()
            .let {
                logger.debug { "create new user login: $userLogin (entityId=$it)" }
                userFactory.createNewUser(it)
            }

    override suspend fun findUserByLogin(userLogin: UserLogin): ENTITY? =
        dao.findUserByLoginCo(userLogin.also { logger.debug { "find user login: $userLogin" } })

    override suspend fun findUserByEmail(userEmail: Email): ENTITY? =
        dao.findUserByEmailCo(userEmail)

    override fun setUserNewStatus(user: ENTITY, status: EntityStatusEnum): ENTITY =
        user.run {
            dao.invalidateCaches(user.login)
            updateStatus(status, isClosedUser(status))
            this
        }

    override fun setUserNewPassword(user: ENTITY, password: UserPassword): ENTITY =
        user.let {
            dao.invalidateCaches(it.login)
            it.copy(password = passwordEncoder.encode(password))
        }

    override fun updateUser(src: User, srcDto: CreateOrUpdateUserRequest): User =
        dto2User(src, srcDto, passwordEncoder)

}
