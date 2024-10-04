package org.dbs.test.container

import api.TestConst.TC_REDIS_IMAGE_DEF
import api.TestConst.TC_REDIS_PORT_DEF
import org.dbs.application.core.service.funcs.StringFuncs.createRandomString
import org.dbs.test.core.AbstractTestContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class RedisTestContainer : AbstractTestContainer() {
    private val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse(TC_REDIS_IMAGE_DEF))
        .withExposedPorts(TC_REDIS_PORT_DEF)

    init {
        redisContainer.start()
    }

    //    @Autowired
    //    ReactiveRedisTemplate<String, String> template;
    override fun overrideApplicationProperties(registry: DynamicPropertyRegistry) = with(registry) {
        add("spring.data.redis.host") { redisContainer.host }
        add("spring.data.redis.port") { redisContainer.firstMappedPort }
        add("spring.data.redis.password") { createRandomString(10) }
    }
}
