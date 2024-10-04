package org.dbs.service

import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit

//@Service
//@ConditionalOnProperty(name = ["spring.data.redis.enabled"], havingValue = "true", matchIfMissing = false)
class RedisUtil<K, V>(private val redisTemplate: RedisTemplate<K, V>) : AbstractApplicationService() {
    private val hashOperation: HashOperations<K, Any, V>
    private val listOperation: ListOperations<K, V>
    private val valueOperations: ValueOperations<K, V>

    init {
        hashOperation = redisTemplate.opsForHash()
        listOperation = redisTemplate.opsForList()
        valueOperations = redisTemplate.opsForValue()
    }
    fun putValue(key: K & Any, value: V & Any) = value.apply { valueOperations[key] = this }

    fun putValueWithExpireTime(key: K & Any, value: V & Any, timeout: Long, unit: TimeUnit) =
        valueOperations.set(key, value, timeout, unit)

    fun getGenericValue(key: K): V? = valueOperations[key as Any]

    fun getValue(key: Any): V? = valueOperations[key]

    fun deleteValue(key: K & Any) {
        if (!redisTemplate.delete(key)) {
            logger.trace("cannot delete key or key not exist: [$key]")
        }
    }

    fun deleteValues(keys: Collection<K>) {
        val countDeleted = redisTemplate.delete(keys)
        if (countDeleted < keys.size) {
            logger.trace("cannot delete key or key not exist; countDeleted: $countDeleted, keys.size: ${keys.size}")
        }
    }

    //fun setExpire(key: K, timeout: Long, unit: TimeUnit) = redisTemplate.expire(key, timeout, unit)
}
