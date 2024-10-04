package org.dbs.test.container

import api.TestConst.TC_MINIO_ACCESS_KEY
import api.TestConst.TC_MINIO_CONSOLE_PORT_DEF
import api.TestConst.TC_MINIO_IMAGE_DEF
import api.TestConst.TC_MINIO_PORT_DEF
import api.TestConst.TC_MINIO_SECRET_KEY
import org.dbs.consts.SysConst.TEN_SECONDS
import org.dbs.test.core.AbstractTestContainer
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.utility.DockerImageName

class MinioTestContainer : AbstractTestContainer() {

    private val minioContainer = GenericContainer(DockerImageName.parse(TC_MINIO_IMAGE_DEF))
        .withEnv("MINIO_ROOT_USER", TC_MINIO_ACCESS_KEY)
        .withEnv("MINIO_ROOT_PASSWORD", TC_MINIO_SECRET_KEY)
        .withCommand("server /data")
        //.withCommand("server --console-address \":$TC_MINIO_CONSOLE_PORT_DEF\" /data")
        .withExposedPorts(TC_MINIO_PORT_DEF, TC_MINIO_CONSOLE_PORT_DEF)
        .waitingFor(
            HttpWaitStrategy()
                .forPath("/minio/health/live")
                .forPort(TC_MINIO_PORT_DEF)
                .withStartupTimeout(TEN_SECONDS)
        )

//        .vo

    init {
        minioContainer.start()
    }

    override fun overrideApplicationProperties(registry: DynamicPropertyRegistry) {
        registry.add("config.filestorage.host") { minioContainer.host }
        registry.add("config.filestorage.accessKey") { TC_MINIO_ACCESS_KEY }
        registry.add("config.filestorage.secretKey") { TC_MINIO_SECRET_KEY }
        registry.add("config.filestorage.expiry") { "1" }
    }
}
