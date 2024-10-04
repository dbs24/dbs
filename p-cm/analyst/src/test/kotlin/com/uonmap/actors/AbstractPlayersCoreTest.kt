package com.uonmap.actors

import api.TestConst.REPEATED_KOTEST_AMOUNT
import api.TestConst.SQL_3S_ACTORS_CORE
import api.TestConst.SQL_TEST_DB_NAME
import api.TestConst.SQL_TEST_DB_USER
import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_REF_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEFAULT_SYS_CURRENCY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_SECRET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_SECRET_KEY_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_DISABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRINGDOC_API_DOCS_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRINGDOC_SWAGGER_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_R2DBC_URL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_CORS_CONFIG_ENABLED
import org.dbs.consts.SuspendNoArg2Mono
import org.dbs.spring.core.api.ServiceLocator.findService
import io.kotest.extensions.spring.SpringExtension
import org.dbs.component.JwtSecurityService
import org.dbs.consts.StringMap
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.LongConsts.MAX_EXPIRY_TIME
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.consts.SysConst.UsersConsts.ROOT_USER
import org.dbs.player.consts.PlayersConsts.Claims.CL_PLAYER_LOGIN
import org.dbs.analyst.service.PlayerService
import org.dbs.test.container.KafkaTestContainer
import org.dbs.test.container.PostgresR2dbcContainer
import org.dbs.test.container.RedisTestContainer
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
import org.dbs.analyst.AnalystApplication as APP
import org.dbs.analyst.config.AnalystConfig as CONFIG
import org.dbs.analyst.service.grpc.CmGrpcService.AnalystService as GRPC

typealias PlayersCoreKoTest = AbstractPlayersCoreTest.() -> Unit

@TestPropertySource(
    properties = [
        "$JUNIT_MODE=true",
        "$SERVER_SSL_ENABLED=$SERVER_SSL_DISABLED",
        "$GRPC_SERVER_PORT=$GRPC_RANDOM_SERVER_PORT",
        "$YML_CORS_CONFIG_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_API_DOCS_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_SWAGGER_ENABLED=$STRING_FALSE",
        "$BUCKET_4J_ENABLED=$STRING_FALSE",
        "$CONFIG_REF_AUTO_SYNCHRONIZE=$STRING_TRUE",
        "$JWT_SECRET_KEY=$JWT_SECRET_KEY_VALUE",
        "$SPRING_R2DBC_URL=$TEST_PG_R2DBC_IMAGE_TAG",
        "$DEFAULT_SYS_CURRENCY=$DEFAULT_SYS_CURRENCY_VALUE"
    ]
)

@ContextConfiguration
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [APP::class]
)
@Import(CONFIG::class)
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
@AutoConfigureWebTestClient
abstract class AbstractPlayersCoreTest(playersCoreKoTest: PlayersCoreKoTest) : AbstractKoTestBehaviorSpec() {

    val entitiesCount = REPEATED_KOTEST_AMOUNT

    // GRPC Test Section
    //------------------------------------------------------------------------------------------------------------------
    val grpcStub by lazy {
        logger.info { "init grpc stub 4 testing: ${javaClass.canonicalName}" }
        findService(GRPC::class.java)
            .also { logger.info { "found grpc service 4 testing: ${it.javaClass.canonicalName}" } }
    }

    //------------------------------------------------------------------------------------------------------------------
    init {
        this.playersCoreKoTest()
    }

    override fun extensions() = setOf(SpringExtension)

    @Value("\${$JWT_SECRET_KEY}")
    private val secretKey = EMPTY_STRING

    // Services
    private val jwtSecurityService by lazy { findService(JwtSecurityService::class) }
    val playerService by lazy { findService(PlayerService::class) }

    fun getJwtToken(claims: StringMap) =
        jwtSecurityService.generateJwt(
            this.javaClass.packageName,
            claims,
            7200, // seconds, 4 test only
            jwtSecurityService.buildKey(secretKey)
        )

    val managerJwt by lazy {
        jwtSecurityService
            .generateJwt(
                javaClass.packageName,
                mapOf(CL_PLAYER_LOGIN to ROOT_USER),
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
            PostgresR2dbcContainer(SQL_TEST_DB_NAME, SQL_TEST_DB_USER, SQL_3S_ACTORS_CORE)
        private val kafkaTestContainer = KafkaTestContainer()
        private val redisTestContainer = RedisTestContainer()
        //private val authServerTestContainer = AuthServerTestContainer()

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(dynamicPropertyRegistry: DynamicPropertyRegistry) {
            postgresR2dbcContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            kafkaTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            redisTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
            //authServerTestContainer.overrideApplicationProperties(dynamicPropertyRegistry)
        }
    }

    fun validatePostgresContainer() =
        require(postgresR2dbcContainer.dbPgContainer.isRunning) { "Postgres is not running" }

}
