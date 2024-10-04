package org.dbs.service.cache.v2

import org.dbs.consts.EntityId
import org.dbs.consts.NoArg2Mono
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SPRING_REDIS_INVALIDATE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_INVALIDATE_HOURS
import org.dbs.consts.SuspendNoArg2Generic
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.entity.core.EntityCacheKeyEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.cache.CacheService
import org.dbs.entity.core.v2.consts.EntityV2Consts
import org.dbs.entity.core.v2.type.Application
import org.dbs.service.RedisUtil
import org.dbs.service.cache.CacheConsts.cacheKeyMask
import org.dbs.service.cache.v2.EntityCacheService.CacheKeyCoreEnum.CC_ENTITY_ID
import org.dbs.service.v2.EntityCoreVal
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.TimeUnit.HOURS

@Service
@Suppress(UNCHECKED_CAST)
class EntityCacheService<V : EntityCoreVal>(redisEntityTemplate: RedisEntityTemplate<V>) :
    AbstractApplicationService(), CacheService<V> {
    @Suppress(UNCHECKED_CAST)
    private val redisUtil by lazy { RedisUtil(redisEntityTemplate) }

    @Value("\${$CONFIG_SPRING_REDIS_INVALIDATE:$SPRING_REDIS_INVALIDATE_HOURS}")
    private val invalidateHours: Long = 24

    enum class CacheKeyCoreEnum : EntityCacheKeyEnum {
        CC_ENTITY_ID {
            override val keyCode = EntityV2Consts.CacheKeyId.ENTITY_ID
            override val entityType = object : EntityTypeEnum {
                override val entityTypeId = 0
                override val entityTypeName = "Generic virtual entity"
                override val module = Application.CORE
            }
            override val cacheCode = "ENTITY.ID"
        }
    }

    fun put(entityId: EntityId, entity: V) {
        logger.debug(
            "put to cache [${CC_ENTITY_ID.cacheCode}], " +
                    "entity[${entityId}, ${entity.javaClass.canonicalName}]"
        )
        //redisUtil.putValue(entityId.cacheKeyMask(), entity)
        redisUtil.putValueWithExpireTime(entityId.cacheKeyMask(), entity, invalidateHours, HOURS)
    }

    override suspend fun getEntity(
        cacheKey: EntityCacheKeyEnum,
        code: String,
        cachedEntity: SuspendNoArg2Generic<V?>
    ): V? =
        getEntityInternalCo(cacheKey, code, cachedEntity)

    override fun invalidateCaches(code: String, vararg cacheKey: EntityCacheKeyEnum) {
        logger.debug { "invalidate caches[${code}], ${cacheKey.map { it.cacheCode }.toList()}" }
        redisUtil.run {
            deleteValues(cacheKey.map { code.cacheKeyMask(it) })
        }
    }

    fun invalidateCaches(entityId: EntityId) =
        redisUtil.run {
            logger.debug { "invalidate cache[${CC_ENTITY_ID.cacheCode}]: [$entityId]" }
            deleteValue(entityId.cacheKeyMask())
        }

    fun invalidateCaches(entityIds: Collection<EntityId>) =
        redisUtil.run {
            logger.debug("invalidate cache[${CC_ENTITY_ID.cacheCode}]: [$entityIds]")
            deleteValues(entityIds.map { it.cacheKeyMask() })
        }

    private fun getEntityInternal(cacheKey: EntityCacheKeyEnum, code: String, func: NoArg2Mono<V>): Mono<V> =
        getCachedEntity(cacheKey, code)?.toMono() ?: run {
            logger.debug("heavy loading ($cacheKey: entity/code='$code')")
            heavyLoad(cacheKey, code, func)
        }

    private suspend fun getEntityInternalCo(
        cacheKey: EntityCacheKeyEnum,
        code: String,
        func: SuspendNoArg2Generic<V?>
    ): V? =
        getCachedEntityCo(cacheKey, code) ?: run {
            logger.debug("heavy loading ($cacheKey: entity/code='$code')")
            heavyLoadCo(cacheKey, code, func)
        }


    private fun getCachedEntity(cacheKey: EntityCacheKeyEnum, code: String): V? =
        try {
            redisUtil.run {
                logger.debug { "load cache[${cacheKey.cacheCode}], entity/code: [$code]" }

                getGenericValue(code.cacheKeyMask(cacheKey))
                    ?.also {
                        logger.debug("found in cache: '$it' (${cacheKey.cacheCode}], entity/code: [$code])")
                    }
            }
        } catch (th: Throwable) {
            logger.warn { "getCachedEntity($cacheKey, $code): $th" }
            invalidateCaches(code, cacheKey)
            null
        }

    private suspend fun getCachedEntityCo(cacheKey: EntityCacheKeyEnum, code: String): V? =
        try {
            redisUtil.run {
                logger.debug { "load cache[${cacheKey.cacheCode}], entity/code: [$code]" }

                getGenericValue(code.cacheKeyMask(cacheKey))
                    ?.also {
                        logger.debug("found in cache: '$it' (${cacheKey.cacheCode}], entity/code: [$code])")
                    }
            }
        } catch (th: Throwable) {
            logger.warn { "getCachedEntity($cacheKey, $code): $th" }
            invalidateCaches(code, cacheKey)
            null
        }


    private suspend fun put(cacheKey: EntityCacheKeyEnum, code: String, entity: V) = redisUtil.run {
        logger.debug("put to cache[${cacheKey}]")
        putValue(code.cacheKeyMask(cacheKey), entity)
    }

    private inline fun heavyLoad(cacheKey: EntityCacheKeyEnum, code: String, cachedEntity: NoArg2Mono<V>): Mono<V> =
        cachedEntity().map { id ->
            //runBlocking { put(cacheKey, code, id) }
            id
        }

    private suspend inline fun heavyLoadCo(
        cacheKey: EntityCacheKeyEnum,
        code: String,
        cachedEntity: SuspendNoArg2Generic<V?>
    ): V? = cachedEntity()
}
