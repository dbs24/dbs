package org.dbs.test.ko

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.NoArg2Unit
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_PORT
import org.dbs.consts.SpringCoreConst.WebClientConsts.UNLIMITED_BUFFER
import org.dbs.consts.SuspendNoArg2Mono
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.rest.api.nio.RequestDto
import org.dbs.spring.core.api.ApplicationBean.Companion.findCanonicalService
import org.dbs.spring.core.api.EntityInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just
import java.time.Duration.ofMillis
import kotlin.random.Random

@Testcontainers
@AutoConfigureWebTestClient
@ContextConfiguration
abstract class AbstractKoTestBehaviorSpec : BehaviorSpec(), Logging {

    @Value("\${reactive.rest.timeout:200000}")
    protected val timeoutDefault: Int = 200000

    @Value("\${$GRPC_SERVER_PORT}")
    protected val testGrpcServerPort: Int = 0

    val grpcServerPort by lazy {
        testGrpcServerPort.also {
            require( it > 0) {"test grpc server port is not defined"}
            logger.info(" Using test grpc server port: $it")
        }
    }

    //override fun extensions() = listOf(SpringExtension, BlockHound())
    override fun extensions() = listOf(SpringExtension)

    private val webTestClient by lazy { findCanonicalService(WebTestClient::class) }

    val random by lazy { Random }
    fun getExistsWebTestClient() = webTestClient.run {
        Hooks.onOperatorDebug()
        mutate()
            .codecs { it.defaultCodecs().maxInMemorySize(UNLIMITED_BUFFER) }
            .responseTimeout(ofMillis(timeoutDefault.toLong()))
            .build()
    }

    //==========================================================================
    suspend fun <T> runTestWithResult(testRunner: SuspendNoArg2Mono<T>): Mono<T> = run {
        logger.info { "execute test ${testRunner.javaClass.simpleName}" }
        testRunner()
    }

    open suspend fun <T> runTest(testRunner: SuspendNoArg2Mono<T>): Mono<T> = runTestWithResult(testRunner)

    fun <E : EntityInfo, A : EntityCore> matchDto2Entity(dto: E, entity: A, matcher: NoArg2Unit): Mono<A> = run {
        logger.info { "matching dto & entity [$dto => $entity]  " }
        matcher()
        just(entity)
    }

    fun <E : RequestDto, A : EntityCore> matchDto2Entity(dto: E, entity: A, matcher: NoArg2Unit): Mono<A> = run {
        logger.info { "matching dto & entity [$dto => $entity]  " }
        matcher()
        just(entity)
    }

}
