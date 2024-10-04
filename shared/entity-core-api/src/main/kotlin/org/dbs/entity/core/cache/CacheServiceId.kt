package org.dbs.entity.core.cache

import org.dbs.entity.core.EntityCacheKeyEnum

interface CacheServiceId {
    fun invalidateCaches(code: String, vararg cacheKey: EntityCacheKeyEnum)
}