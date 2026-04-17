package org.dbs.goods.service

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.consts.Email
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER_PASS
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.goods.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.goods.UserCore.isClosedUser
import org.dbs.goods.UserLogin
import org.dbs.goods.UserPassword
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.r2dbcPersistenceService
import org.dbs.service.v2.ActionEvent
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now
import org.dbs.goods.dao.UserDao as DAO
import org.dbs.goods.model.user.User as ENTITY

@Service
@Lazy(false)
class UserService(
    val dao: DAO,
    val passwordEncoder: PasswordEncoder,
    val userFactory: UserFactory,
    val eventPublisher: ApplicationEventPublisher,
) : AbstractApplicationService() {

    override fun initialize() = super.initialize().also {
        runBlocking {
            getOrCreateDefaultRootUser().subscribeMono()
        }
    }

    suspend fun getOrCreateDefaultRootUser(): Mono<ENTITY> =
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
            .flatMap { r2dbcPersistenceService.saveEntity(it) }
            .doOnSuccess { user ->
                eventPublisher.publishEvent(
                    ActionEvent(
                        entityId = user.userId,
                        entityTypeId = user.entityType.entityTypeId,
                        actionCodeId = EA_CREATE_OR_UPDATE_USER.actionCodeId,
                        remoteAddr = "system",
                        actionNote = "Root user created",
                    )
                )
            }
    }

    suspend fun saveUser(
        user: ENTITY,
        actionEnum: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): ENTITY = r2dbcPersistenceService.saveEntity(user).awaitSingle()
        .also {
            eventPublisher.publishEvent(
                ActionEvent(
                    entityId = it.userId,
                    entityTypeId = it.entityType.entityTypeId,
                    actionCodeId = actionEnum.actionCodeId,
                    remoteAddr = remoteAddr,
                    actionNote = actionNote,
                )
            )
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
        user.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedUser(status)) now() else user.closeDate,
        ).also {
            dao.invalidateCaches(user.login)
            it.justCreated.update(user.justCreated.value)
        }

    fun setUserNewPassword(user: ENTITY, password: UserPassword): ENTITY =
        user.copy(password = passwordEncoder.encode(password), modifyDate = now())
            .also {
                dao.invalidateCaches(user.login)
                it.justCreated.update(user.justCreated.value)
            }
}
