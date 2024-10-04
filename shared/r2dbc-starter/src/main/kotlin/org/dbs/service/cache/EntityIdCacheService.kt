package org.dbs.service.cache

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.consts.EntityId
import org.dbs.consts.NoArg2Mono
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SPRING_REDIS_INVALIDATE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_INVALIDATE_HOURS
import org.dbs.consts.SysConst.LONG_NULL
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.cache.CacheServiceId
import org.dbs.service.RedisUtil
import org.dbs.service.cache.CacheConsts.cacheKey2code
import org.dbs.service.cache.CacheConsts.cacheKeyMask
import org.dbs.service.cache.v2.EntityCacheService.CacheKeyCoreEnum.CC_ENTITY_ID
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.TimeUnit.HOURS

@Service
@Suppress(UNCHECKED_CAST)
class EntityIdCacheService(redisTemplate: RedisTemplate<String, String>) :
    AbstractApplicationService(), CacheServiceId {
    @Suppress(UNCHECKED_CAST)
    private val redisUtil by lazy { RedisUtil(redisTemplate) }

    @Value("\${$CONFIG_SPRING_REDIS_INVALIDATE:$SPRING_REDIS_INVALIDATE_HOURS}")
    private val invalidate: Long = 24

    fun getEntityId(cacheKey: EntityCacheKeyEnum, code: String, func: NoArg2Mono<EntityId>): Mono<EntityId> =
        getCachedEntityId(cacheKey, code).run {
            (this?.let { this.toLong() } ?: LONG_NULL).toMono()
                .switchIfEmpty { runBlocking { heavyLoad(cacheKey, code, func) } }
        }

    fun getEntityCode(cacheKey: EntityCacheKeyEnum, entityId: Long, func: NoArg2Mono<String>): Mono<String> =
        getCachedEntityCode(cacheKey, entityId).run {
            (this?.let { this } ?: STRING_NULL).toMono()
                .switchIfEmpty { runBlocking { heavyLoad(cacheKey, entityId, func) } }
        }

    override fun invalidateCaches(code: String, vararg entityCacheKey: EntityCacheKeyEnum): Unit = runBlocking {
        logger.debug("invalidate caches[${code}], ${entityCacheKey.map { it.cacheCode }.toList()}")
        launch {
            redisUtil.run {
                deleteValues(entityCacheKey.map { code.cacheKeyMask(it) })
            }
        }
    }

    fun invalidateCache(entityId: EntityId) = redisUtil.run {
        logger.debug("invalidate cache[${CC_ENTITY_ID.cacheCode}]: [${entityId}]")
        deleteValue(entityId.cacheKeyMask())
    }

    private fun getCachedEntityId(cache: EntityCacheKeyEnum, code: String) = redisUtil.run {
        logger.debug("load cache[${cache.cacheCode}], code: [${code}]")
        getGenericValue(code.cacheKeyMask(cache))?.also {
            logger.debug("found in cache: '$it' ([${cache.cacheCode}], code: [${code}])")
        }
    }

    private fun getCachedEntityCode(cache: EntityCacheKeyEnum, entityId: Long) = redisUtil.run {
        logger.debug("load cache[${cache.cacheCode}], entityId: [${entityId}]")
        getGenericValue(entityId.cacheKey2code(cache))?.also {
            logger.debug("found in cache: '$it' ([${cache.cacheCode}], entityId: [${entityId}])")
        }
    }

    private fun put(cache: EntityCacheKeyEnum, code: String, entityId: EntityId) = redisUtil.run {
        logger.debug("put to cache[${cache.cacheCode}], code: '$code', entityId: $entityId]")
        putValueWithExpireTime(code.cacheKeyMask(cache), entityId.toString(), invalidate, HOURS)
    }

    private fun put(cache: EntityCacheKeyEnum, entityId: EntityId, code: String) = redisUtil.run {
        logger.debug("put to cache[${cache.cacheCode}], entityId: $entityId, code: '$code'")
        putValueWithExpireTime(entityId.cacheKey2code(cache), code, invalidate, HOURS)
    }

    private suspend inline fun heavyLoad(
        cacheKey: EntityCacheKeyEnum,
        code: String,
        func: NoArg2Mono<EntityId>
    ): Mono<Long> = func().map { entityId -> put(cacheKey, code, entityId); entityId }

    private suspend inline fun heavyLoad(
        cacheKey: EntityCacheKeyEnum,
        entityId: EntityId,
        crossinline func: NoArg2Mono<String>
    ): Mono<String> =
        coroutineScope { func().map { entityCode -> put(cacheKey, entityId, entityCode); entityCode } }
}
