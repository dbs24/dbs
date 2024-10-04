package org.dbs.service.v2

import io.r2dbc.spi.Parameters
import io.r2dbc.spi.R2dbcType.*
import io.r2dbc.spi.Result
import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.api.PersistenceService
import org.dbs.application.core.service.funcs.ReflectionFuncs.createPkgClassesCollection
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.*
import org.dbs.consts.RestHttpConsts.URI_HTTPS
import org.dbs.consts.SpringCoreConst.PropertiesNames.ASYNC_ALG_STORE_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.ASYNC_ALG_STORE_ENABLED_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_REF_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.NEXT_VAL_ACTION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SELECT_NEXT_VAL_ACTION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_NEXT_VAL_CMD
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.LONG_ZERO
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.entity.core.*
import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.entity.core.v2.model.Entity
import org.dbs.entity.core.v2.model.EntityState
import org.dbs.entity.core.v2.type.EntityCoreInitializer
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.cacheKeys
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityActionEnums
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityTypes
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.findEntityStatus
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.service.consts.ActionExec
import org.dbs.service.dao.EntityDao
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.concat
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now
import java.time.LocalTime.MIN

@Service
@Lazy(false)
@EnableTransactionManagement
class R2dbcPersistenceService(
    private val databaseClient: DatabaseClient,
    private val entityDao: EntityDao,
    private val reactiveTransactionManager: ReactiveTransactionManager,
    private val cacheService: EntityCacheService<EntityCoreVal>,
) : AbstractApplicationService(), PersistenceService {

    @Value("\${$CONFIG_REF_AUTO_SYNCHRONIZE:$STRING_TRUE}")
    private val autoSynchronize = false

    @Value("\${$SPRING_R2DBC_NEXT_VAL_CMD:$SELECT_NEXT_VAL_ACTION}")
    private val nextValCmd = SELECT_NEXT_VAL_ACTION

    @Value("\${$ASYNC_ALG_STORE_ENABLED:$ASYNC_ALG_STORE_ENABLED_VALUE}")
    private val asyncAlgStore: Boolean = false

    @Value("\${$SPRING_R2DBC_URL}")
    val r2dbcUrl = EMPTY_STRING

    val entities4store by lazy { EntitiesChannel() }

    val histEntities4store by lazy { EntitiesHistChannel() }

    fun <T> initCoreEnums(clazz: Class<T>, collection: MutableCollection<T>) {
        createPkgClassesCollection(ALL_PACKAGES, clazz).filter { it.isEnum }
            .forEach {
                it.enumConstants.forEach {
                    //logger.debug { "initialize: $it" }
                    collection.add(it)
                }
            }
    }

    override fun initialize() = super<AbstractApplicationService>.initialize().also {

        addUrl4LivenessTracking(r2dbcUrl.replace("r2dbc:postgresql://", URI_HTTPS), javaClass.simpleName)

        // all instances of EntityCoreInitializer
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

        // entity statuses
        initCoreEnums(EntityStatusEnum::class.java, entityStatuses)
        logger.debug { "validate entity statuses: [${entityStatuses}]" }
        require(entityStatuses.isNotEmpty()) { " Entity statuses list is empty" }
        fromIterable(entityStatuses)
            .noDuplicates({ it.entityStatusId })
            .count()
            .subscribeMono()

        // entity statuses
        initCoreEnums(EntityActionEnum::class.java, entityActionEnums)
        logger.debug { "validate entity actions: [${entityActionEnums}]" }
        require(entityActionEnums.isNotEmpty()) { " Entity actions list is empty" }
        fromIterable(entityActionEnums)
            .noDuplicates({ it.actionCodeId })
            .noDuplicates({ it.actionName })
            .count()
            .subscribeMono()

        // entity cache key enums
        initCoreEnums(EntityCacheKeyEnum::class.java, cacheKeys)
        logger.debug { "validate entity actions: [${entityActionEnums}]" }
        require(cacheKeys.isNotEmpty()) { " Entity caches list is empty" }
        fromIterable(cacheKeys)
            .noDuplicates({ it.cacheKeyCodeId })
            .noDuplicates({ it.cacheCode })
            .count()
            .subscribeMono()

        if (autoSynchronize) with(entityDao) {
            synchronizeEntityTypes()
            synchronizeEntityStatuses()
            synchronizeActionCodes()
        }

    }

    suspend fun <T : EntityCoreVal> getEntityCore(entity: T): EntityState = entity.run {
        (getEntityStateInternalCo(entityId)
            ?: error("core entity not found (entityId: ${entityId})"))
    }

    fun generateNewEntityIdV2(): Mono<EntityId> = generateNewEntityId()

    companion object {

        const val SQL_INSERT_INTO_ENTITIES = """
            INSERT INTO core_entities (entity_id, entity_type_id, entity_status_id, create_date, modify_date) 
              VALUES ($1, $2, $3, $4, $5)
            """
        const val SQL_UPDATE_ENTITIES = """
            UPDATE core_entities SET modify_date = $1, entity_status_id = $2, close_date = $3 
              WHERE entity_id = $4
            """

        const val SQL_INSERT_INTO_ACTIONS = """
            INSERT INTO core_actions (action_id, entity_id, action_code, user_id, execute_date, action_address, err_msg, action_duration, notes)
              VALUES ($NEXT_VAL_ACTION, $1, $2, $3, $4, $5, $6, $7, $8)
            """

        val emptyFlux by lazy { Flux.empty<Result>() }

    }

    override fun generateNewEntityId(): Mono<EntityId> =
        databaseClient.sql(nextValCmd)
            .map { row -> row.get(0, java.lang.Long::class.java) } //
            .one()
            .map { newId -> newId.toLong() }

    suspend fun getEntityStateInternalCo(entityId: EntityId): EntityState? =
        databaseClient.sql("SELECT entity_status_id, modify_date, close_date FROM core_entities WHERE entity_id= $1")
            .bind(0, Parameters.`in`(BIGINT, entityId))
            .map { row ->
                EntityState(
                    findEntityStatus(row.get(0, java.lang.Integer::class.java)!!.toInt()),
                    row.get(1, OperDate::class.java)!!,
                    row.get(2, OperDate::class.java)
                )
            }.one().awaitSingle()

    override val transactionalOperator: TransactionalOperator
        get() = TransactionalOperator.create(reactiveTransactionManager)

    fun <T : EntityCoreVal> saveEntity(abstractEntity: T): Mono<T> =
        entityDao.saveCoreEntity(abstractEntity)
            .flatMap { entity: Entity ->
                logger.debug(
                    "${if (abstractEntity.justCreated.value) "insert new" else "update"} entity: ${entity.entityId}, " +
                            "[${abstractEntity.javaClass.canonicalName}]"
                )
                entityDao.saveEntity(abstractEntity)
            }

    fun <T : EntityCoreVal> saveEntities(entities: Collection<T>): Flux<T> =
        entityDao.saveEntities(entities)

    fun invalidateCaches(code: String, vararg entityCache: EntityCacheKeyEnum) {
        cacheService.invalidateCaches(code, *entityCache)
    }

    fun <T : EntityCoreVal> saveEntityHist(abstractEntity: T): Mono<T> = entityDao.saveEntityHist(abstractEntity)

    suspend fun <T : EntityCoreVal> saveEntityHistCo(abstractEntity: T): T = entityDao.saveEntityHistCo(abstractEntity)

    //==================================================================================================================
    private fun saveAction(action: Action) = entityDao.saveAction(action)


    /**
     * Save input entities without add it to cache, also save one action for entity batch
     * @param entities entities to save
     * @param actionCode action code enum
     * @param parentEntityId parent entity id for action
     * @param remoteAddr remote address
     * @param actionNote action note
     */

    val actExecOpt: ActionExec<EntityCoreVal> =
        { abstractEntities, actionCode, remoteAddr, actionNote ->

            if (abstractEntities.isEmpty()) LONG_ZERO.toMono()
            else databaseClient.inConnectionMany {
                // core entities

                //runBlocking {

                require(abstractEntities.isNotEmpty()) { "##### executeAction: entities list is empty" }

                val now by lazy { now() }

                val stmtInsertCoreEntities by lazy { it.createStatement(SQL_INSERT_INTO_ENTITIES) }
                val stmtUpdateCoreEntities by lazy { it.createStatement(SQL_UPDATE_ENTITIES) }
                val stmtCoreActions by lazy { it.createStatement(SQL_INSERT_INTO_ACTIONS) }

                var newCoreEntites = false
                var updCoreEntites = false
                var addActionStmt = false
                var coreEntitiesAdded = 0
                var coreEntitiesUpdated = 0
                val entClass4logger by lazy { abstractEntities.first().javaClass }

                if (abstractEntities.size > 1)
                    logger.debug { "executeActionNew: ${abstractEntities.size}" }

                abstractEntities.asSequence().forEach { me ->
                    with(me) {

                        val status4update = if (newEntityStatus.isInitialized()) newEntityStatus.value.entityStatusId
                        else entityCore.entityStatus.entityStatusId

                        if (justCreated.value) {
                            if (newCoreEntites) stmtInsertCoreEntities.add()
                            else
                                newCoreEntites = true

                            coreEntitiesAdded++

                            stmtInsertCoreEntities
                                .bind(0, Parameters.`in`(BIGINT, entityId))
                                .bind(1, Parameters.`in`(INTEGER, entityType.entityTypeId))
                                .bind(2, Parameters.`in`(INTEGER, status4update))
                                .bind(3, Parameters.`in`(TIMESTAMP, now))
                                .bind(4, Parameters.`in`(TIMESTAMP, now))

                        } else {

                            if (updCoreEntites) stmtUpdateCoreEntities.add()
                            else
                                updCoreEntites = true
                            coreEntitiesUpdated++

                            stmtUpdateCoreEntities
                                .bind(0, Parameters.`in`(TIMESTAMP, now))
                                .bind(1, Parameters.`in`(INTEGER, status4update))
                                .bind(2, Parameters.`in`(TIMESTAMP, now.takeIf { isClosed.value }))
                                .bind(3, Parameters.`in`(BIGINT, entityId))
                        }

                        if (addActionStmt) stmtCoreActions.add()
                        else
                            addActionStmt = true

                        //logger.debug { "Insert newCoreActions" }
                        stmtCoreActions
                            .bind(0, Parameters.`in`(BIGINT, entityId)) // entity_id
                            .bind(1, Parameters.`in`(INTEGER, actionCode.actionCodeId)) //
                            .bind(2, Parameters.`in`(INTEGER, 1)) // user_id
                            .bind(3, Parameters.`in`(TIMESTAMP, now))// execute_date
                            .bind(4, Parameters.`in`(VARCHAR, remoteAddr)) //remote_address,
                            .bind(5, Parameters.`in`(VARCHAR, STRING_NULL)) // errMsg
                            .bind(6, Parameters.`in`(TIME, MIN)) // errMsg
                            .bind(7, Parameters.`in`(VARCHAR, actionNote)) // notes

                        logger.debug(
                            "executeAction: $actionCode [$remoteAddr, ${if (justCreated.value) "create new" else "update"} " +
                                    "entity (entityId=${entityId}) = $me]"
                        )
                    }
                }

                logger.debug {
                    "${entClass4logger.simpleName}: core entities added: $coreEntitiesAdded, " +
                            "updated: $coreEntitiesUpdated, total entities: ${coreEntitiesAdded + coreEntitiesUpdated}, " +
                            "(${entClass4logger.canonicalName})"
                }

                emptyFlux.mergeWith(if (newCoreEntites) stmtInsertCoreEntities.execute() else emptyFlux)
                    .mergeWith(if (updCoreEntites) stmtUpdateCoreEntities.execute() else emptyFlux)
                    .mergeWith(stmtCoreActions.execute())
                    .flatMap {
                        //logger.debug { "rows inserted/updated: ${it.rowsUpdated}" }
                        it.rowsUpdated
                    }
            }.count()
                .flatMapMany {
                    // store entities
                    val modifiedEntities = createCollection<Mono<EntityCoreVal>>()

                    abstractEntities.asSequence().forEach { entity ->
                        modifiedEntities.add(entityDao.entityTemplate.run {
                            if (entity.justCreated.value) insert(entity) else update(entity)
                        })
                    }

                    // run in separate thread
                    //saveEntitiesToCache(abstractEntities)

                    concat(modifiedEntities)
                }
                .count()
        }

    fun <T : EntityCoreVal> executeAction(
        abstractEntity: T,
        ac: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): Mono<T> = executeAction(setOf(abstractEntity), ac, remoteAddr, actionNote)
        .map { abstractEntity }

    fun <T : EntityCoreVal> executeAction(
        abstractEntities: Collection<T>,
        ac: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): Mono<Long> =
        if ((asyncAlgStore) && (abstractEntities.size > 1)) {
            entities4store.push(AsyncEntitiesVal(AsyncEntities(abstractEntities, ac, remoteAddr, actionNote)))
            LONG_ZERO.toMono()
        } else
            actExecOpt(abstractEntities, ac, remoteAddr, actionNote)


    suspend fun <T : EntityCoreVal> executeActionCo(
        abstractEntity: T,
        ac: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): T = executeActionCo(setOf(abstractEntity), ac, remoteAddr, actionNote).let { abstractEntity }

    suspend fun <T : EntityCoreVal> executeActionCo(
        abstractEntities: Collection<T>,
        ac: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): Long =
        if ((asyncAlgStore) && (abstractEntities.size > 1)) {
            entities4store.push(AsyncEntitiesVal(AsyncEntities(abstractEntities, ac, remoteAddr, actionNote)))
            LONG_ZERO
        } else
            actExecOpt(abstractEntities, ac, remoteAddr, actionNote).awaitSingle()


    private fun createAction(
        newActionId: ActionId,
        ac: ActionCodeId,
        entityId: EntityId,
        userId: EntityId,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ) = Action(newActionId, entityId, userId, ac, now(), remoteAddr, UNKNOWN, MIN, actionNote)

    fun doOnError(throwable: Throwable) = log(throwable) { "Persistence exception ($throwable)" }

}
