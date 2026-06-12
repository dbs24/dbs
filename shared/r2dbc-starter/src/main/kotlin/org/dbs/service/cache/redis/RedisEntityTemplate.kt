package org.dbs.service.cache.redis

import org.dbs.entity.core.v2.model.EntityCore
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Service

@Service
@Suppress("UNCHECKED_CAST")
class RedisEntityTemplate<V : EntityCore>(lcf: LettuceConnectionFactory) :
    RedisTemplate<String, V>() {

    init {
        connectionFactory = lcf
        keySerializer = StringRedisSerializer()
        hashKeySerializer = GenericToStringSerializer(EntityCore::class.java)

        val jacksonSerializer = GenericJacksonJsonRedisSerializer.builder().build()

        hashValueSerializer = jacksonSerializer
        valueSerializer = jacksonSerializer

        afterPropertiesSet()
    }
}
