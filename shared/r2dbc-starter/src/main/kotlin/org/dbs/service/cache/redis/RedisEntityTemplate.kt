package org.dbs.service.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Service

@Service
@Suppress(UNCHECKED_CAST)
class RedisEntityTemplate<V : EntityCoreVal>(lcf: LettuceConnectionFactory, objectMapper: ObjectMapper) :
    RedisTemplate<String, V>() {

    init {
        connectionFactory = lcf
        keySerializer = StringRedisSerializer()
        hashKeySerializer = GenericToStringSerializer(EntityCoreVal::class.java)
        hashValueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
    }
}
