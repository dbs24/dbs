package org.dbs.test.ko

import api.TestConst.SQL_TEST_DB_NAME
import api.TestConst.SQL_TEST_DB_USER
import com.google.protobuf.GeneratedMessage
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import net.devh.boot.grpc.server.config.GrpcServerProperties
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_SECRET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_SECRET_KEY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.KOTEST_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.REFERENCES_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_DISABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRINGDOC_API_DOCS_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRINGDOC_SWAGGER_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_CORS_CONFIG_ENABLED
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.ext.SpringFuncs.fromErrString
import org.dbs.test.container.KafkaTestContainer
import org.dbs.test.container.PostgresR2dbcContainer
import org.dbs.test.container.RedisTestContainer
import org.dbs.test.core.SysTestConsts.Grpc.GRPC_RANDOM_SERVER_PORT
import org.dbs.test.core.SysTestConsts.Postgres.TEST_PG_R2DBC_IMAGE_TAG
import org.dbs.validator.ErrorInfo
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.TimeUnit

@TestPropertySource(
    properties = [
        "grpc.server.port=0",
        "grpc.server.security.enabled=false",
        "$JUNIT_MODE=true",
        "$KOTEST_MODE=true",
        "$SERVER_SSL_ENABLED=$SERVER_SSL_DISABLED",
        "$GRPC_SERVER_PORT=$GRPC_RANDOM_SERVER_PORT",
        "$YML_CORS_CONFIG_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_API_DOCS_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_SWAGGER_ENABLED=$STRING_FALSE",
        "$BUCKET_4J_ENABLED=$STRING_FALSE",
        "$REFERENCES_AUTO_SYNCHRONIZE=$STRING_TRUE",
        "$JWT_SECRET_KEY=$JWT_SECRET_KEY_VALUE",
        "$SPRING_R2DBC_URL=$TEST_PG_R2DBC_IMAGE_TAG",
        "$DEFAULT_SYS_CURRENCY=$DEFAULT_SYS_CURRENCY_VALUE"
    ]
)
@ContextConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
@AutoConfigureWebTestClient
abstract class BaseGrpcSpec : StringSpec(), Logging {

    @Autowired
    lateinit var grpcServerProperties: GrpcServerProperties

    override val extensions = listOf(SpringExtension)

    protected lateinit var channel: ManagedChannel

    init {
        beforeSpec {
            val port = grpcServerProperties.port

            require (port > 0) { "gRPC server port is not assigned! Current port: $port. Check if gRPC server started." }

            logger.info(" gRPC test server port: $port")

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

    suspend fun getErrorsFromStub(getExceptionFunc: suspend () -> GeneratedMessage): Collection<ErrorInfo> {

        val exception = shouldThrow<StatusException> {
            getExceptionFunc()
        }

        // 1. Конвертируем исключение в Proto Status
        val rpcStatus = StatusProto.fromThrowable(exception) ?: error("No RPC status found")

        logger.info { "rpcStatus: $rpcStatus.m" }

        rpcStatus.message shouldBe "Validation failed"

        // 1. Проверяем основной статус
        exception.status.code shouldBe Status.Code.INVALID_ARGUMENT

        // 2. Извлекаем trailers (metadata)
        val trailers = Status.trailersFromThrowable(exception) ?: error("No trailers found in exception")

        // 3. Собираем все значения ошибок в список
        return trailers.keys()
            .filter { it.startsWith("error-") }
            .map { keyName ->
                val key = io.grpc.Metadata.Key.of(keyName, Metadata.BINARY_BYTE_MARSHALLER)
                String(trailers.get(key) ?: byteArrayOf()).fromErrString()
            }

    }

    // Методы расширения для конкретных тестов
    abstract fun initStubs(channel: ManagedChannel)

    companion object {

        val postgresR2dbcContainer = PostgresR2dbcContainer(SQL_TEST_DB_NAME, SQL_TEST_DB_USER)
        private val kafkaTestContainer = KafkaTestContainer()
        private val redisTestContainer = RedisTestContainer()

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            postgresR2dbcContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            kafkaTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            redisTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
        }
    }
}
