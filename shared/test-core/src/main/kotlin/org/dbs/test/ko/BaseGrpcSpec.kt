package org.dbs.test.ko

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.kotest.assertions.throwables.shouldThrowAny
import net.devh.boot.grpc.server.config.GrpcServerProperties
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.ext.SpringFuncs.fromErrString
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.TimeUnit


abstract class BaseGrpcSpec: BaseSpec(), Logging {

    class GrpcEntityFactory<Builder, Message>(
        val newBuilder: () -> Builder,
        val build: (Builder) -> Message
    ) {
        inline fun create(configure: Builder.() -> Unit): Message {
            return build(newBuilder().apply(configure))
        }
    }

    @Autowired
    lateinit var grpcServerProperties: GrpcServerProperties

    override val source = "gRPC"

    protected lateinit var channel: ManagedChannel

    init {
        beforeSpec {
            val port = grpcServerProperties.port

            require(port > 0) { "gRPC server port is not assigned! Current port: $port. Check if gRPC server started." }

            logger.info("gRPC test server port: $port")

            channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .build()
            initStubs(channel)
        }

        afterSpec {
            channel.shutdown().awaitTermination(2, TimeUnit.SECONDS)
        }

        beforeTest {
            //clearDatabase()
        }
    }

    suspend fun <T> (suspend () -> T).shouldFailWithValidation(): ErrorBox {
        val ex = shouldThrowAny { this.invoke() }

        // Проверяем, что это именно gRPC ошибка
        if (ex !is StatusException && ex !is StatusRuntimeException) {
            throw ex
        }

        val trailers = Status.trailersFromThrowable(ex)
            ?: error("Expected gRPC trailers with errors, but got none")

        val errors = trailers.keys()
            .filter { it.startsWith("error-") }
            .map { keyName ->
                val key = Metadata.Key.of(keyName, Metadata.BINARY_BYTE_MARSHALLER)
                String(trailers.get(key)!!).fromErrString()
            }

        return ErrorBox(errors)
    }

    suspend fun <T> (suspend () -> T).shouldFailWithInternalError() {
        val ex = shouldThrowAny { this.invoke() }

        // Проверяем, что это именно gRPC ошибка
        if (ex !is StatusException && ex !is StatusRuntimeException) {
            throw ex
        }

        val trailers = Status.trailersFromThrowable(ex)
            ?: error("Expected gRPC trailers with errors, but got none")

        val firstKey = trailers.keys().first()

        val key = Metadata.Key.of(firstKey, Metadata.BINARY_BYTE_MARSHALLER)
        val errMsg = String(trailers.get(key)!!)

        logger.error { "found internal error: $errMsg" }

        require( firstKey == "internal-error-bin" ) {

            "Expected 'internal-error-bin' key, but was: $firstKey ($errMsg)"
        }

    }

    // Методы расширения для конкретных тестов
    abstract fun initStubs(channel: ManagedChannel)

}
