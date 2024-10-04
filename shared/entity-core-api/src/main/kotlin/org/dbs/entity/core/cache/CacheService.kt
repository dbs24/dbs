package org.dbs.entity.core.cache

import org.dbs.consts.SuspendNoArg2Generic
import org.dbs.entity.core.EntityCacheKeyEnum

interface CacheService<V>: CacheServiceId {

    suspend fun getEntity(cacheKey: EntityCacheKeyEnum, code: String, cachedEntity: SuspendNoArg2Generic<V?>): V?

}