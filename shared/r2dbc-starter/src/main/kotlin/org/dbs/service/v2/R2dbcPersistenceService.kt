package org.dbs.service.v2

import org.dbs.api.PersistenceService
import org.dbs.application.core.service.funcs.ReflectionFuncs.createPkgClassesCollection
import org.dbs.consts.RestHttpConsts.URI_HTTPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_REF_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.entity.core.*
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.cacheKeys
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityActionEnums
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityTypes
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.service.dao.EntityDao
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono

@Service
@Lazy(false)
@EnableTransactionManagement
@DependsOn("flywayInitializer")
class R2dbcPersistenceService(
    private val entityDao: EntityDao,
    private val reactiveTransactionManager: ReactiveTransactionManager,
    private val cacheService: EntityCacheService<out EntityCore>,
) : AbstractApplicationService(), PersistenceService {

    @Value("\${$CONFIG_REF_AUTO_SYNCHRONIZE:$STRING_TRUE}")
    private val autoSynchronize = false

    @Value("\${$SPRING_R2DBC_URL}")
    val r2dbcUrl = EMPTY_STRING

    fun <T> initCoreEnums(clazz: Class<T>, collection: MutableCollection<T>) {
        createPkgClassesCollection(ALL_PACKAGES, clazz).filter { it.isEnum }
            .forEach {
                it.enumConstants.forEach {
                    collection.add(it)
                }
            }
    }

    override fun initialize() = super<AbstractApplicationService>.initialize().also {

        addUrl4LivenessTracking(r2dbcUrl.replace("r2dbc:postgresql://", URI_HTTPS), javaClass.simpleName)

        val allInstances = createPkgClassesCollection(
            ALL_PACKAGES,
            EntityCoreInitializer::class.java
        ).map { it.kotlin.objectInstance }

        require(allInstances.isNotEmpty()) { " EntityCoreInitializer instances not found" }
        allInstances.forEach {
            logger.debug { "Initialize entityCore model [${it?.javaClass?.canonicalName}]" }
        }

        initCoreEnums(EntityTypeEnum::class.java, entityTypes)
        logger.debug { "validate entity types: [${entityTypes}]" }
        require(entityTypes.isNotEmpty()) { " Entity types list is empty" }

        fromIterable(entityTypes)
            .noDuplicates({ it.entityTypeId }, { it.entityTypeName })
            .count()
            .subscribeMono()

        initCoreEnums(EntityStatusEnum::class.java, entityStatuses)
        logger.debug { "validate entity statuses: [${entityStatuses}]" }
        require(entityStatuses.isNotEmpty()) { " Entity statuses list is empty" }
        fromIterable(entityStatuses)
            .noDuplicates({ it.entityStatusId })
            .count()
            .subscribeMono()

        initCoreEnums(EntityActionEnum::class.java, entityActionEnums)
        logger.debug { "validate entity actions: [${entityActionEnums}]" }
        require(entityActionEnums.isNotEmpty()) { " Entity actions list is empty" }
        fromIterable(entityActionEnums)
            .noDuplicates({ it.actionCodeId })
            .noDuplicates({ it.actionName })
            .count()
            .subscribeMono()

        initCoreEnums(EntityCacheKeyEnum::class.java, cacheKeys)
        logger.debug { "validate entity cache keys: [${cacheKeys}]" }
        require(cacheKeys.isNotEmpty()) { " Entity caches list is empty" }
        fromIterable(cacheKeys)
            .noDuplicates({ it.cacheKeyCodeId })
            .noDuplicates({ it.cacheCode })
            .count()
            .subscribeMono()

        if (autoSynchronize) with(entityDao) {
            logger.debug { "synchronize system references" }
            synchronizeEntityTypes()
            synchronizeEntityStatuses()
            synchronizeActionCodes()
        }
    }

    override val transactionalOperator: TransactionalOperator
        get() = TransactionalOperator.create(reactiveTransactionManager)

    fun <T : EntityCore> saveEntity(abstractEntity: T): Mono<T> =
        entityDao.saveEntity(abstractEntity)
            .doOnSuccess {
                logger.debug(
                    "${if (abstractEntity.entityId == null) "insert new" else "update"} " +
                            "entity: ${abstractEntity.entityId} [${abstractEntity.javaClass.canonicalName}]"
                )
            }

    fun <T : EntityCore> saveEntityHist(abstractEntity: T): Mono<T> = entityDao.saveEntityHist(abstractEntity)

    suspend fun <T : EntityCore> saveEntityHistCo(abstractEntity: T): T = entityDao.saveEntityHistCo(abstractEntity)

    fun invalidateCaches(code: String, vararg entityCache: EntityCacheKeyEnum) {
        cacheService.invalidateCaches(code, *entityCache)
    }

    fun doOnError(throwable: Throwable) = log(throwable) { "Persistence exception ($throwable)" }
}
