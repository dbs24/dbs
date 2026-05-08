package org.dbs.tree.user

import api.TestConst.SQL_TEST_DB_NAME
import api.TestConst.SQL_TEST_DB_USER
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.devh.boot.grpc.server.config.GrpcServerProperties
import org.dbs.tree.client.CreateOrUpdateUserRequest
import org.dbs.tree.client.UserServiceGrpcKt
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
import org.dbs.mgmt.TreeApplication
import org.dbs.mgmt.config.TreeConfig
import org.dbs.test.container.KafkaTestContainer
import org.dbs.test.container.PostgresR2dbcContainer
import org.dbs.test.container.RedisTestContainer
import org.dbs.test.core.SysTestConsts.Grpc.GRPC_RANDOM_SERVER_PORT
import org.dbs.test.core.SysTestConsts.Postgres.TEST_PG_R2DBC_IMAGE_TAG
import org.dbs.tree.repo.user.UserRepo
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.TimeUnit


@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [TreeApplication::class]
)

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
@Import(TreeConfig::class)
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
@AutoConfigureWebTestClient
class UserGrpcIntegrationTest(
) : StringSpec() {

    @Autowired
    lateinit var grpcServerProperties: GrpcServerProperties
    @Autowired
    lateinit var userRepo: UserRepo

    // Подключаем расширение Kotest для Spring
    override val extensions = listOf(SpringExtension)

    init {
        lateinit var channel: ManagedChannel
        lateinit var stub: UserServiceGrpcKt.UserServiceCoroutineStub

        beforeSpec {
            val port = grpcServerProperties.port

            require (port > 0) { "gRPC server port is not assigned! Current port: $port. Check if gRPC server started." }

            println(" gRPC server port: $port")

            channel = ManagedChannelBuilder
                .forAddress("localhost", port)
                .usePlaintext()
                .keepAliveTime(30, TimeUnit.SECONDS) // Отправка keepalive каждые 30 с
                .keepAliveTimeout(10, TimeUnit.SECONDS) // Таймаут keepalive
                .maxInboundMessageSize(10 * 1024 * 1024) // 10 МБ
                .build()
            stub = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

        }

        afterSpec {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        }

        beforeTest {
            userRepo.deleteAll()
        }

        "Success: Create user via gRPC network call" {
            val request = CreateOrUpdateUserRequest.newBuilder()
                .setLogin("valid_user")
                .setEmail("test@example.com")
                .setPassword("Strong1Password")
                .build()

            val response = stub.createOrUpdateUser(request)

            response.userLogin shouldBe "valid_user"
            response.email shouldBe "test@example.com"
            userRepo.findByLogin("valid_user") shouldNotBe null
        }
    }

    companion object {

        val postgresR2dbcContainer = PostgresR2dbcContainer(SQL_TEST_DB_NAME, SQL_TEST_DB_USER)
        private val kafkaTestContainer = KafkaTestContainer()
        private val redisTestContainer = RedisTestContainer()
        //private val mailTestContainer = MailServerTestContainer()
        //private val actorsTestContainer = ActorsServerTestContainer()
        //private val authServerTestContainer = AuthServerTestContainer()

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            postgresR2dbcContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            kafkaTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            redisTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            //mailTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            //actorsTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            //authServerTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
        }
    }
}
