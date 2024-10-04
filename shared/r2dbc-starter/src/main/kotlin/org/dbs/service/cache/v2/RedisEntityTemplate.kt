package org.dbs.service.cache.v2

import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Service

@Service
@Suppress(UNCHECKED_CAST)
class RedisEntityTemplate<V : EntityCoreVal>(lcf: LettuceConnectionFactory) :
    RedisTemplate<String, V>() {

    init {
        connectionFactory = lcf
        keySerializer = StringRedisSerializer()
        hashKeySerializer = GenericToStringSerializer(EntityCoreVal::class.java)
        hashValueSerializer = JdkSerializationRedisSerializer()
        valueSerializer = JdkSerializationRedisSerializer()
    }
}
