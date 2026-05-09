package org.dbs.service.dao

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.EntityId
import org.dbs.entity.core.ActionCode
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatus
import org.dbs.entity.core.EntityType
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityActionEnums
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityStatuses
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityTypes
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.dbs.service.repo.ActionCodeRepository
import org.dbs.service.repo.ActionRepo
import org.dbs.service.repo.EntityStatusRepository
import org.dbs.service.repo.EntityTypeRepository
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import kotlin.system.measureTimeMillis

@Service
@Lazy(false)
class EntityDao(
    val entityTemplate: R2dbcEntityTemplate,
    private val entityTypeRepository: EntityTypeRepository,
    private val entityStatusRepository: EntityStatusRepository,
    private val actionCodeRepository: ActionCodeRepository,
    val actionRepo: ActionRepo,
) : DaoAbstractApplicationService() {

    suspend fun findByEntityIdAndActionCode(entityId: EntityId, action: EntityActionEnum) =
        actionRepo.findByEntityIdAndActionCode(entityId, action.actionCodeId)

    suspend fun requireActions(entityId: EntityId, action: EntityActionEnum) {
        findByEntityIdAndActionCode(entityId, action).firstOrNull()
            ?: error("action(s) not found for entity: $entityId ")
    }

    fun <T : EntityCore> saveEntity(entity: T): Mono<T> =
        if (entity.entityId == null)
            entityTemplate.insert(entity)
        else
            entityTemplate.update(entity)

    fun <T : EntityCore> saveEntities(entities: Collection<T>): Flux<T> = Flux.concat(
        createCollection { savedEntities ->
            entities.forEach { savedEntities.add(saveInternal(it)) }
        })

    private fun <T : EntityCore> saveInternal(entity: T): Mono<T> =
        if (entity.entityId == null) entityTemplate.insert(entity) else entityTemplate.update(entity)

    fun <T : EntityCore> saveEntityHist(entity: T): Mono<T> = entityTemplate.insert(entity)

    suspend fun <T : EntityCore> saveEntityHistCo(entity: T): T = entityTemplate.insert(entity).awaitSingle()

    //==================================================================================================================
    fun synchronizeEntityTypes() = measureTimeMillis {
        // validate and save all entity types
        fromIterable(entityTypes)
            .map { EntityType(it.entityTypeId.toInt(), it.entityTypeName, it.module.name) }
            .publishOn(parallelScheduler)
            .noDuplicates({ it.entityTypeId }, { it.entityTypeName })
            .synchronizeReference(
                entityTypeRepository,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })

        // validate database
        entityTypeRepository.findAll()
            .validateDb { rec -> entityTypes.count { rec.entityTypeId == it.entityTypeId.toInt() } == 1 }.count()
            .subscribeMono()
    }
        .also {
            logger.debug { "synchronizeEntityTypes: took $it ms" }
        }


    fun synchronizeEntityStatuses() = measureTimeMillis {
        // validate and save all entity types
        fromIterable(entityStatuses)
            .map { EntityStatus(it.entityStatusId, it.entityType.entityTypeId, it.entityStatusName) }
            .publishOn(parallelScheduler)
            .noDuplicates({ it.entityStatus })
            .synchronizeReference(
                entityStatusRepository,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })

        // validate database
        entityStatusRepository.findAll()
            .validateDb { rec -> entityStatuses.count { rec.entityStatus == it.entityStatusId } == 1 }.count()
            .subscribeMono()
    }
        .also {
            logger.debug { "synchronizeEntityStatuses: took $it ms" }
        }

    //
//
    fun synchronizeActionCodes() = measureTimeMillis {
        fromIterable(entityActionEnums)
            .map { ActionCode(it.actionCodeId, it.actionName, it.actionName, false) }
            .noDuplicates({ it.actionCode }, { it.actionName }, { it.appName })
            .synchronizeReference(
                actionCodeRepository,
                { existItem, preparedItem -> existItem.actionCode == preparedItem.actionCode },
                { preparedItem -> preparedItem.copy() })

        // validate database
        actionCodeRepository.findAll()
            .validateDb { rec -> entityActionEnums.count { rec.actionCode == it.actionCodeId } == 1 }.count()
            .subscribeMono()

    }.also {
        logger.debug { "synchronizeActionCode: took $it ms" }
    }

}
