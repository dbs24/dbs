package org.dbs.tree

import io.kotest.common.KotestInternal
import io.kotest.core.annotation.Isolate
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.test.ko.BaseSpec
import org.dbs.tree.config.TreeConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Import
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.core.RedisTemplate

@OptIn(KotestInternal::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
@Suppress("unused")
@Isolate // Заменяем устаревший/внутренний @Isolate на стандартный Spring @Isolated
abstract class BaseCacheSpec(val cacheName: String) : BaseSpec(), Logging {

    @Autowired lateinit var cacheManager: CacheManager
    @Autowired lateinit var redisTemplate: RedisTemplate<String, Any>

    override val source = "cache"

    init {
        beforeSpec {
            cacheManager.getCache(cacheName)?.clear()
        }
    }

    protected fun getTestingCache() = cacheManager.getCache(cacheName)

    protected fun verifyKeyInRedis(key: Any) {
        val redisKey = "$cacheName::$key"
        val rawDataInRedis = redisTemplate.opsForValue().get(redisKey)
        rawDataInRedis shouldNotBe null
    }

    protected fun verifyCacheManagerType() {
        cacheManager.shouldBeInstanceOf<RedisCacheManager>()
    }
}