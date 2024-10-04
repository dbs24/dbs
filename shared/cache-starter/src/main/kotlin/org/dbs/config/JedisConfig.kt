package org.dbs.config

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_PWD
import org.dbs.consts.SysConst.COMPONENT_PACKAGE
import org.dbs.consts.SysConst.RSOCKET_PACKAGE
import org.dbs.consts.SysConst.SERVICE_PACKAGE
import org.dbs.consts.SysConst.UNKNOWN
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.ReactiveMapSessionRepository
import org.springframework.session.ReactiveSessionRepository
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap


@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@ComponentScan(basePackages = [SERVICE_PACKAGE, RSOCKET_PACKAGE, COMPONENT_PACKAGE])
@EnableRedisWebSession
@EnableSpringWebSession
@Order(HIGHEST_PRECEDENCE)
class JedisConfig(
    @Value("\${$JUNIT_MODE:false}") private val junitMode: Boolean,
    @Value("\${$SPRING_REDIS_HOST}") private val redisHost: String,
    @Value("\${$SPRING_REDIS_PORT}") private val redisPort: Int,
    @Value("\${$SPRING_REDIS_PWD:$UNKNOWN}") private val redisPass: String
) : AbstractCacheConfig() {
    // https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#get-started:first-steps:spring
    // https://javascopes.com/spring-data-redis-tutorial-43067fc2/
    // https://dzone.com/articles/implementation-of-redis-in-micro-servicespring-boo
    @Bean
    @Primary
    fun reactiveConnectionFactory(rsc: RedisStandaloneConfiguration): ReactiveRedisConnectionFactory =
        LettuceConnectionFactory(rsc, lettuceCfg()).apply { afterPropertiesSet() }

    @Bean
    fun reactiveSessionRepository(): ReactiveSessionRepository<*> = ReactiveMapSessionRepository(ConcurrentHashMap())

    @Bean
    fun redisStandaloneConfiguration() =
        RedisStandaloneConfiguration().apply {
            hostName = redisHost
            port = redisPort
            if (!junitMode && redisPass != UNKNOWN) {
                password = RedisPassword.of(redisPass.toCharArray())
            }
        }

    private fun lettuceCfg() = LettuceClientConfiguration.builder().build()

    private fun redisCacheConfiguration() = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(4))

    @Bean
    fun redisCacheManager(connectionFactory: LettuceConnectionFactory): RedisCacheManager =
        RedisCacheManager.builder(connectionFactory)
            .transactionAware()
            .cacheDefaults(redisCacheConfiguration())
            .build()

    @Bean
    @ConditionalOnMissingBean(name = ["redisTemplate"])
    fun redisTemplate(lcf: LettuceConnectionFactory): RedisTemplate<String, Any> = RedisTemplate<String, Any>().apply {
        logger.debug("create default redis template ($redisHost:$redisPort:$redisPass)")
        connectionFactory = lcf
        keySerializer = StringRedisSerializer()
        hashKeySerializer = GenericToStringSerializer(Any::class.java)
        hashValueSerializer = JdkSerializationRedisSerializer()
        valueSerializer = JdkSerializationRedisSerializer()
    }

    override fun initialize() = super.initialize().also { addHost4LivenessTracking(redisHost, redisPort, javaClass.simpleName) }

}
