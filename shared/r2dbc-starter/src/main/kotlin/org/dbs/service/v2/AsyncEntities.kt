package org.dbs.service.v2

import org.dbs.config.AbstractChannel
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.v2.model.EntityCore

// Legacy async batching structures — kept for backward compatibility with AsyncExecutionService.
// Will be removed once all modules migrate to ActionEvent-based approach.
data class AsyncEntities<T : EntityCore>(
    val abstractEntities: Collection<T>,
    val ac: EntityActionEnum,
    val remoteAddr: IpAddress,
    val actionNote: StringNote,
)

data class HistEntity<T : EntityCore>(val entity: T)

@JvmInline
value class AsyncEntitiesVal<T : EntityCore>(val asyncEntities: AsyncEntities<T>)

@JvmInline
value class HistEntitiesVal<T : EntityCore>(val histEntity: HistEntity<T>)

class EntitiesChannel : AbstractChannel<AsyncEntitiesVal<EntityCore>>()

class EntitiesHistChannel : AbstractChannel<HistEntitiesVal<EntityCore>>()
