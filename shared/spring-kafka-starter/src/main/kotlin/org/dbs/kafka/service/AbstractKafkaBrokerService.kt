package org.dbs.kafka.service

import org.dbs.consts.RestHttpConsts.URI_LOCALHOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_BOOSTRAP_SERVERS
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value

abstract class AbstractKafkaBrokerService : AbstractApplicationService() {

    @Value("\${$KAFKA_BOOSTRAP_SERVERS:$URI_LOCALHOST}")
    private val bootstrapServers = URI_LOCALHOST

    override fun initialize() = super.initialize().also {
        bootstrapServers.also {
            logger.debug { "register kafka bootstrap-servers: $it" }
        }.split(",")
            .forEach { host ->
                host.replace("PLAINTEXT://", EMPTY_STRING).let { address ->

                    val colonPos = address.indexOf(":")
                    require(colonPos != -1) { "Address [$address] must contain colon (:) symbol" }
                    val remoteHost = address.substring(0, colonPos)
                    val port = address.substring(colonPos + 1)

                    logger.debug { "$address - add host '$remoteHost:$port' 4 tracking" }

                    if (colonPos > 0) {
                        addHost4LivenessTracking(remoteHost, port.toInt(), javaClass.simpleName)
                    } else {
                        logger.error { "$address: Invalid entrance ($bootstrapServers)" }
                    }
                }
            }
    }

}
