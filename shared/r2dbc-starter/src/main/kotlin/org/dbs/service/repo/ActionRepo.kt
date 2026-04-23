package org.dbs.service.repo

import kotlinx.coroutines.flow.Flow
import org.dbs.consts.ActionId
import org.dbs.consts.EntityId
import org.dbs.entity.core.Action
import org.dbs.entity.core.v2.consts.ActionCodeId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ActionRepo : CoroutineCrudRepository<Action, ActionId?> {
    suspend fun findByEntityIdAndActionCode(entityId: EntityId, actionCode: ActionCodeId) : Flow<Action>
}
