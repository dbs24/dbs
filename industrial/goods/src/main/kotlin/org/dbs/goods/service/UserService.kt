package org.dbs.goods.service

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.Email
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.r2dbcPersistenceService
import org.dbs.goods.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.goods.UserCore.isClosedUser
import org.dbs.goods.UserLogin
import org.dbs.goods.UserPassword
import org.dbs.service.v2.EntityCoreVal.Companion.executeAction
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.service.v2.EntityCoreValExt.updateStatus
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.goods.dao.UserDao as DAO
import org.dbs.goods.model.user.User as ENTITY

@Service
@Lazy(false)
class UserService(
    val dao: DAO,
    val passwordEncoder: PasswordEncoder,
    val userFactory: UserFactory,
) : AbstractApplicationService() {
    //override fun initialize() = super.initialize().also { getOrCreateDefaultRootUser().subscribeMono() }

    override fun initialize() = super.initialize().also {

        runBlocking {
            getOrCreateDefaultRootUser().subscribeMono()
        }
    }

    suspend fun getOrCreateDefaultRootUser(): Mono<ENTITY> =  // crete Root role if not exists
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
    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
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

    suspend fun createNewUser(userLogin: UserLogin): ENTITY =
        generateNewEntityId()
            .let {
                logger.debug { "create new user login: $userLogin (entityId=$it)" }
                userFactory.createNewUser(it)
            }

    suspend fun findUserByLogin(userLogin: UserLogin): ENTITY? =
        dao.findUserByLogin(userLogin.also { logger.debug { "find user login: $userLogin" } })

    suspend fun findUserByEmail(userEmail: Email): ENTITY? =
        dao.findUserByEmailCo(userEmail)

    fun setUserNewStatus(user: ENTITY, status: EntityStatusEnum): ENTITY =
        user.run {
            dao.invalidateCaches(user.login)
            updateStatus(status, isClosedUser(status))
            this
        }

    fun setUserNewPassword(user: ENTITY, password: UserPassword): ENTITY =
        user.let {
            dao.invalidateCaches(it.login)
            it.copy(password = passwordEncoder.encode(password))
        }
}
