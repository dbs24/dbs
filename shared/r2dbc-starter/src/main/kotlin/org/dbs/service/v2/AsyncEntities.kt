package org.dbs.service.v2

import org.dbs.config.AbstractChannel
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.EntityActionEnum

data class AsyncEntities<T : EntityCoreVal>(
    val abstractEntities: Collection<T>,
    val ac: EntityActionEnum,
    val remoteAddr: IpAddress,
    val actionNote: StringNote,
)

data class HistEntity<T : EntityCoreVal>(val entity: T)
@JvmInline
value class AsyncEntitiesVal<T : EntityCoreVal>(val asyncEntities: AsyncEntities<T>)

@JvmInline
value class HistEntitiesVal<T : EntityCoreVal>(val histEntity: HistEntity<T>)
class EntitiesChannel : AbstractChannel<AsyncEntitiesVal<EntityCoreVal>>()

class EntitiesHistChannel : AbstractChannel<HistEntitiesVal<EntityCoreVal>>()
