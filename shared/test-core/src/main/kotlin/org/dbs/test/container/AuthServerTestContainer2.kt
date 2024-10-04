package org.dbs.test.container

import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_BOOSTRAP_SERVERS
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.test.core.AbstractTestContainer2
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName


class AuthServerTestContainer2 : AbstractTestContainer2() {

    @Suppress(UNCHECKED_CAST)
    override fun <T : GenericContainer<*>> createContainer(): T = let {

        val imageName = "git2.uonmap.com:5555/smartsafeschool/backend/sss_services/auth_layer/auth_server"
            .also {
                logger.debug { "try 2 create image '$it'" }
            }

        GenericContainer(
            (DockerImageName.parse(imageName))
//                .withImagePullPolicy(PullPolicy.alwaysPull())
        ) as T
    }

    override fun overrideApplicationProperties(registry: DynamicPropertyRegistry) {
        registry.add(KAFKA_BOOSTRAP_SERVERS) {

//            authServerContainer.bootstrapServers.also {
//                logger.debug { "testcontainer kafka bootstrap-servers: $it" }
//            }
        }
    }
}
