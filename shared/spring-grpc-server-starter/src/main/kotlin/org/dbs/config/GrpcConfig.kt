package org.dbs.config

import net.devh.boot.grpc.server.event.GrpcServerStartedEvent
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class GrpcConfig : Logging {

    @EventListener
    fun onServerStarted(event: GrpcServerStartedEvent) {
        logger.debug {
            "gRPC Server started [${event.address}:${event.port}], " +
                "services: ${getServiceInfo(event)}"
        }
    }

    private fun getServiceInfo(event: GrpcServerStartedEvent): String = run {
        val sb = StringBuilder("\n")
        event.server.services.forEachIndexed { index, it ->
            val methodNames = it.methods.map { it.methodDescriptor.bareMethodName }
            sb.append("service ${index + 1}: ${it.serviceDescriptor.name}; methods: ${methodNames}\n")
        }
        sb.toString().dropLast(1)
    }
}
