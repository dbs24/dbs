package org.dbs.test.container

import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_BOOSTRAP_SERVERS
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_DISABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.consts.SysEnvConst.SysProperties.USER_DIR
import org.dbs.test.core.AbstractTestContainer2
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.images.builder.ImageFromDockerfile
import org.testcontainers.utility.DockerImageName
import java.lang.System.getProperty
import kotlin.io.path.Path


class ActorsServerTestContainer : AbstractTestContainer2() {

    @Suppress(UNCHECKED_CAST)
    override fun <T : GenericContainer<*>> createContainer(): T =
        getProperty(USER_DIR).also { logger.debug { "user.dir = '$it'" } }
            .let {
                val workDir = "/home/user/Projects/uonmap/sss_services/3s-actors/actors"
                val filePath = "$workDir/Dockerfile"
                val path = Path(filePath)
                val port = 64433

                val image = ImageFromDockerfile().withDockerfile(path)

                val imageName = DockerImageName.parse(image.get())
                val times = 1
                val network = Network.newNetwork()

                GenericContainer(imageName)
                    .withNetwork(network)
                    .withEnv(
                        SERVER_SSL_ENABLED,
                        SERVER_SSL_DISABLED
                    )
                    .withExposedPorts(port)
//                    .withAccessToHost(true)
//                    .withNetworkAliases("test.k11dev.tech")
                    .withStartupAttempts(times)
                    /*.waitingFor(
                        Wait.forHttp("/actuator/health")
                            .forPort(port)
                            .forStatusCode(OK.value())
                            .withStartupTimeout(TIMEOUT_60_SEC)
                    )*/ as T
            }

    override fun overrideApplicationProperties(registry: DynamicPropertyRegistry) {
        registry.add(KAFKA_BOOSTRAP_SERVERS) {

//            authServerContainer.bootstrapServers.also {
//                logger.debug { "testcontainer kafka bootstrap-servers: $it" }
//            }
        }
    }
}
