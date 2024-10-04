package org.dbs.service

import io.grpc.CallCredentials
import io.grpc.CallOptions.DEFAULT
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder.forAddress
import io.grpc.Metadata
import io.grpc.kotlin.AbstractCoroutineStub
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.GenericArg2Unit
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_BEARER_AUTHORIZATION
import org.dbs.consts.Jwt
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.SysConst.TIMEOUT_10000_MILLIS_LONG
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.ext.LoggerFuncs.logRequest
import org.dbs.protobuf.core.ResponseCode
import org.dbs.protobuf.core.ResponseCode.RC_OK
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.security.api.JwtSecurityServiceApi
import java.io.Closeable
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.function.Predicate
import kotlin.concurrent.thread
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.jvm.jvmErasure
import kotlin.system.measureTimeMillis

typealias GrpcCall<K, V> = suspend (K) -> V
typealias GrpcFlowCall<K, V> = (K) -> V
typealias Call<V> = suspend () -> V
typealias FlowCall<V> = () -> V
typealias StreamJob = suspend () -> Unit

abstract class AbstractGrpcClientService<T : AbstractCoroutineStub<T>>(
    private val grpcUrl: String,
    private val grpcPort: Int,
    private val useClientSsl: Boolean = true,
) : AbstractApplicationService(), Closeable {

    private val ymlConfig by lazy { findCanonicalService(GrpcYmlConfig::class) }
    private val clientSsl by lazy { ymlConfig.useSsl.takeIf { useClientSsl } ?: useClientSsl }
    private val streamJobs by lazy { createCollection<Thread>() }
    private val blockingJob: GenericArg2Unit<StreamJob> = { runBlocking { it() } }
    fun addGrpcStreamJob(sj: StreamJob) {

        if (env.junitMode) blockingJob(sj) else
            thread(start = false) {
                blockingJob(sj)
            }.apply {
                start()
                streamJobs.add(this)
                streamJobs.removeIf { !it.isAlive or it.isInterrupted }
            }
    }

    @Suppress(UNCHECKED_CAST)
    private val client: T by lazy {
        require(isHostPortAvailable(grpcUrl, grpcPort)) {
            "grpc stub client is not available ($grpcUrl:$grpcPort, " +
                    "${this.javaClass.simpleName}) "
        }

        logger.debug {
            "init gRPC stub client ($grpcUrl:$grpcPort, ssl:$clientSsl), ${javaClass.canonicalName}, " +
                    "junitMode:${env.junitMode}"
        }
        (this::class
            .allSupertypes.first { it.classifier == AbstractGrpcClientService::class }
            .arguments.first().type!!
            .jvmErasure
            .constructors.first { it.parameters.size == 2 }
            .also { if (!env.junitMode) addHost4LivenessTracking(grpcUrl, grpcPort, javaClass.simpleName) }
            .call(
                forAddress(grpcUrl, grpcPort)
                    .let { if (clientSsl) it else it.usePlaintext() }
                    .build(),
                DEFAULT.withCallCredentials(BearerJwtCredentials("$BEARER${jwtSecurityService.getServiceJwt()}"))
            ) as T)
            .also { logger.debug { "gRPC stub client was created ($grpcUrl:$grpcPort, ssl=$clientSsl): ${it.javaClass.canonicalName}" } }
    }

    private val jwtSecurityService by lazy { findService(JwtSecurityServiceApi::class) }

    fun <K : AbstractCoroutineStub<T>, V> grpcCall(rpcCall: GrpcCall<T, V>) = rpcCall.run {
        val tryCall: Call<V> = {
            val response: V
            measureTimeMillis { response = invoke(client) }.also {
                logger.logRequest(it, ymlConfig.maxTimeExec)
                { "grpc call ($grpcUrl:$grpcPort), ${client.javaClass.simpleName}" }
            }
            response
        }
        runBlocking {
            runCatching {
                tryCall()
            }.getOrElse {
                // retry call
                logger.error { it }.run {
                    if (isHostPortAvailable(grpcUrl, grpcPort)) {
                        logger.warn { "retry call 2 $grpcUrl:$grpcPort, ssl: $useClientSsl, ${client.javaClass.simpleName}" }
                    }
                    tryCall()
                }
            }
        }
    }

    fun <K : AbstractCoroutineStub<T>, V> grpcFlowCall(rpcCall: GrpcFlowCall<T, V>) = rpcCall.run {
        val tryCall: FlowCall<V> = {
            val response: V
            measureTimeMillis { response = invoke(client) }.also {
                logger.logRequest(it, ymlConfig.maxTimeExec)
                { "grpc flow call ($grpcUrl:$grpcPort), ${client.javaClass.simpleName}" }
            }
            response
        }
        runCatching {
            tryCall()
        }.getOrElse {
            // retry call
            logger.error { it }.run {
                if (isHostPortAvailable(grpcUrl, grpcPort)) {
                    logger.warn { "retry flow call 2 $grpcUrl:$grpcPort, ssl: $useClientSsl, ${client.javaClass.simpleName}" }
                }
                tryCall()
            }
        }
    }

    override fun initialize() = super.initialize().also {
        addHost4LivenessTracking(grpcUrl, grpcPort, javaClass.simpleName)
        logger.debug { "initialize grpc client ($grpcUrl:$grpcPort), ${javaClass.canonicalName}" }
    }

    override fun close() {
        logger.debug { "close grpc client ($grpcUrl:$grpcPort), ${javaClass.canonicalName}" }
        (client.channel as ManagedChannel).shutdown().awaitTermination(TIMEOUT_10000_MILLIS_LONG, MILLISECONDS)
    }

    companion object {
        val validGrpcResponse by lazy { Predicate { responseCode: ResponseCode -> responseCode == RC_OK } }
        val badGrpcResponse by lazy { Predicate { responseCode: ResponseCode -> responseCode != RC_OK } }
    }
}

// for internal calls
class BearerJwtCredentials(private val serviceJwt: Jwt) : CallCredentials() {
    override fun applyRequestMetadata(
        requestInfo: RequestInfo,
        appExecutor: Executor,
        applier: MetadataApplier,
    ) {
        appExecutor.execute {
            applier.apply(Metadata()
                .also { it.put(GRPC_BEARER_AUTHORIZATION, serviceJwt) })
        }
    }
}
