package org.dbs.service.cache

import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.service.cache.v2.EntityCacheService.CacheKeyCoreEnum.CC_ENTITY_ID


object CacheConsts {
    fun Long.cacheKeyMask() = "${CC_ENTITY_ID.cacheCode}_${this}"

    fun Long.cacheKey2code(cacheKey: EntityCacheKeyEnum) = "${cacheKey.cacheCode}_${this}"
    fun String.cacheKeyMask(cacheKey: EntityCacheKeyEnum) = "${cacheKey.cacheCode}_${this}"
}
