package org.dbs.service.dao

import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.EntityId
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.service.repo.ActionRepo
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
@Lazy(false)
class EntityDao(
    val entityTemplate: R2dbcEntityTemplate,
    val actionRepo: ActionRepo,
) : DaoAbstractApplicationService() {

    suspend fun findByEntityIdAndActionCode(entityId: EntityId, action: EntityActionEnum) =
        actionRepo.findByEntityIdAndActionCode(entityId, action.actionCodeId)

    fun <T : EntityCore> saveEntity(entity: T): Mono<T> =
        entity.entityId?.let {
            entityTemplate.update(entity)
        } ?: entityTemplate.insert(entity)


    fun <T : EntityCore> saveEntities(entities: Collection<T>): Flux<T> = Flux.concat(
        createCollection { savedEntities ->
            entities.forEach { savedEntities.add(saveInternal(it)) }
        })

    private fun <T : EntityCore> saveInternal(entity: T): Mono<T> =
        if (entity.entityId == null) entityTemplate.insert(entity) else entityTemplate.update(entity)

    fun <T : EntityCore> saveEntityHist(entity: T): Mono<T> = entityTemplate.insert(entity)

    suspend fun <T : EntityCore> saveEntityHistCo(entity: T): T = entityTemplate.insert(entity).awaitSingle()


}
