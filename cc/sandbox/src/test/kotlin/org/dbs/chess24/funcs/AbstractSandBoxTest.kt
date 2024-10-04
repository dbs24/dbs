package org.dbs.chess24.funcs

import api.TestConst.SQL_CHESS24_SANDBOX_DB_SCRIPT
import api.TestConst.SQL_TEST_DB_NAME
import api.TestConst.SQL_TEST_DB_USER
import io.kotest.extensions.spring.SpringExtension
import org.dbs.component.JwtSecurityService
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_SECURITY
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_SSL_DISABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_SECRET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_SECRET_KEY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.KOTEST_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.REFERENCES_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SANDBOX_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SANDBOX_SERVICE_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_DISABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRINGDOC_API_DOCS_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRINGDOC_SWAGGER_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_LAZY_INIT
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_CORS_CONFIG_ENABLED
import org.dbs.consts.StringMap
import org.dbs.consts.SuspendNoArg2Mono
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.LongConsts.MAX_EXPIRY_TIME
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.sandbox.SandBoxApplication
import org.dbs.sandbox.config.SandBoxConfig
import org.dbs.sandbox.service.GrpcSandBoxClientService
import org.dbs.sandbox.service.InviteService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.test.container.KafkaTestContainer
import org.dbs.test.container.PostgresR2dbcContainer
import org.dbs.test.container.RedisTestContainer
import org.dbs.test.core.SysTestConsts.Postgres.TEST_PG_R2DBC_IMAGE_TAG
import org.dbs.test.ko.AbstractKoTestBehaviorSpec
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource

typealias ChessKoTest = AbstractSandBoxTest.() -> Unit
const val GRPC_TEST_PORT = 15000

@TestPropertySource(
    properties = [
        "$JUNIT_MODE=true",
        "$KOTEST_MODE=true",
        "$SERVER_SSL_ENABLED=$SERVER_SSL_DISABLED",
        "$GRPC_SERVER_PORT=$GRPC_TEST_PORT",
        "$GRPC_SERVER_SECURITY=$GRPC_SERVER_SSL_DISABLED",
        "$SPRING_BOOT_LAZY_INIT=false",
        "$YML_CORS_CONFIG_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_API_DOCS_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_SWAGGER_ENABLED=$STRING_FALSE",
        "$BUCKET_4J_ENABLED=$STRING_FALSE",
        "$REFERENCES_AUTO_SYNCHRONIZE=$STRING_TRUE",
        "$JWT_SECRET_KEY=$JWT_SECRET_KEY_VALUE",
        "$SPRING_R2DBC_URL=$TEST_PG_R2DBC_IMAGE_TAG",
        "$DEFAULT_SYS_CURRENCY=$DEFAULT_SYS_CURRENCY_VALUE",
        "$SANDBOX_SERVICE_HOST=$URI_LOCALHOST",
        "$SANDBOX_SERVICE_PORT=$GRPC_TEST_PORT",
    ]
)

@ContextConfiguration
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [SandBoxApplication::class]
)
@Import(SandBoxConfig::class)
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
@AutoConfigureWebTestClient
abstract class AbstractSandBoxTest(chessCoreKoTest: ChessKoTest) : AbstractKoTestBehaviorSpec() {
    init {
        this.chessCoreKoTest()
    }

    @Value("\${$JWT_SECRET_KEY}")
    private val secretKey = EMPTY_STRING

    // Services
    private val jwtSecurityService by lazy { findService(JwtSecurityService::class) }
    val inviteService by lazy { findService(InviteService::class) }
    val grpcSandBoxClientService by lazy { findService(GrpcSandBoxClientService::class) }

    fun getJwtToken(claims: StringMap) =
        jwtSecurityService.generateJwt(
            this.javaClass.packageName,
            claims,
            3600, // seconds
            jwtSecurityService.buildKey(secretKey)
        )

    val playerJwt by lazy {
        jwtSecurityService
            .generateJwt(
                javaClass.packageName,
                mapOf(ROOT_USER to ROOT_USER),
                MAX_EXPIRY_TIME,
                jwtSecurityService.buildKey(secretKey)
            )
    }

    override suspend fun <T> runTest(testRunner: SuspendNoArg2Mono<T>) = run {
        validatePostgresContainer()
        runTestWithResult(testRunner)
    }

    companion object {

        val postgresR2dbcContainer =
            PostgresR2dbcContainer(SQL_TEST_DB_NAME, SQL_TEST_DB_USER, SQL_CHESS24_SANDBOX_DB_SCRIPT)
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

    fun validatePostgresContainer() =
        require(postgresR2dbcContainer.dbPgContainer.isRunning) { "Postgres is not running" }

}
