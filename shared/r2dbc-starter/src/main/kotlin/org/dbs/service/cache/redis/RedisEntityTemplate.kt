package org.dbs.service.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.entity.core.v2.model.EntityCore
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Service

@Service
@Suppress(UNCHECKED_CAST)
class RedisEntityTemplate<V : EntityCore>(lcf: LettuceConnectionFactory, objectMapper: ObjectMapper) :
    RedisTemplate<String, V>() {

    init {
        connectionFactory = lcf
        keySerializer = StringRedisSerializer()
        hashKeySerializer = GenericToStringSerializer(EntityCore::class.java)
        hashValueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
    }
}
