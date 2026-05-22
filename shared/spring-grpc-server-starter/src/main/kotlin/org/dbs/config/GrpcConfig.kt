package org.dbs.config

import io.grpc.ServerCall
import io.grpc.kotlin.CoroutineContextServerInterceptor
import net.devh.boot.grpc.server.event.GrpcServerStartedEvent
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.RemoteAddressCoroutineContext
import org.dbs.ext.GrpcFuncs.getProcedureName
import org.dbs.ext.GrpcFuncs.getRemoteAddress
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext

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
        event.server.services.forEachIndexed { index, service ->
            val methodNames = service.methods.map { it.methodDescriptor.bareMethodName }
            sb.append("service ${index + 1}: ${service.serviceDescriptor.name}; methods: ${methodNames}\n")
        }
        sb.toString().dropLast(1)
    }
}

@Component
@GrpcGlobalServerInterceptor
class IpCoroutineInterceptor : CoroutineContextServerInterceptor() {

    override fun coroutineContext(
        call: ServerCall<*, *>,
        headers: io.grpc.Metadata
    ): CoroutineContext =
        RemoteAddressCoroutineContext(call.getRemoteAddress(call.getProcedureName(), headers))
}
