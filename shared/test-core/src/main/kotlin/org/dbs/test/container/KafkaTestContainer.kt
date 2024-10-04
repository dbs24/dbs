package org.dbs.test.container

import api.TestConst.TC_KAFKA_IMAGE_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_BOOSTRAP_SERVERS
import org.dbs.test.core.AbstractTestContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class KafkaTestContainer : AbstractTestContainer() {
    private val kafkaContainer = KafkaContainer(DockerImageName.parse(TC_KAFKA_IMAGE_DEF))

    init {
        kafkaContainer.start()
    }

    override fun overrideApplicationProperties(registry: DynamicPropertyRegistry) {
        registry.add(KAFKA_BOOSTRAP_SERVERS) {
            kafkaContainer.bootstrapServers.also {
                logger.debug { "testcontainer kafka bootstrap-servers: $it" }
            }
        }
    }
}
