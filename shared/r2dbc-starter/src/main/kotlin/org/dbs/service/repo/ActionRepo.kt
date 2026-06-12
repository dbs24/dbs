package org.dbs.service.repo

import kotlinx.coroutines.flow.Flow
import org.dbs.consts.ActionCodeId
import org.dbs.consts.ActionId
import org.dbs.consts.EntityId
import org.dbs.entity.core.EntityAction
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ActionRepo : CoroutineCrudRepository<EntityAction, ActionId> {
    suspend fun findByEntityIdAndActionCode(entityId: EntityId, actionCode: ActionCodeId) : Flow<EntityAction>
}
