package org.dbs.test.ko

import api.TestConst.SQL_TEST_DB_NAME
import api.TestConst.SQL_TEST_DB_USER
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
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
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.ext.SpringFuncs.fromErrString
import org.dbs.test.container.KafkaTestContainer
import org.dbs.test.container.PostgresR2dbcContainer
import org.dbs.test.container.RedisTestContainer
import org.dbs.test.core.SysTestConsts.Grpc.GRPC_RANDOM_SERVER_PORT
import org.dbs.test.core.SysTestConsts.Postgres.TEST_PG_R2DBC_IMAGE_TAG
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.Field
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

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

    @Autowired
    lateinit var databaseClient: DatabaseClient

    override val extensions = listOf(SpringExtension)

    protected lateinit var channel: ManagedChannel

    init {
        beforeSpec {
            val port = grpcServerProperties.port

            require(port > 0) { "gRPC server port is not assigned! Current port: $port. Check if gRPC server started." }

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

    private val fieldsCache by lazy { ConcurrentHashMap<KClass<*>, Set<String>>() }
    private val ignoredTechnicalFields by lazy { setOf("type", "status") }

    // Вспомогательный класс для связки свойства и проверки
    class PropertyValidator<T, V>(
        val property: KProperty1<T, V>,
        val assertion: T.(V) -> Unit
    )

    // Инфиксная функция для синтаксиса: User::login verify { it shouldBe "admin" }
    infix fun <T, V> KProperty1<T, V>.verify(assertion: T.(V) -> Unit) =
        PropertyValidator(this, assertion)

    suspend fun <T : EntityCore> verifyModifiedEntity(
        entity: T?,
        actionEnum: EntityActionEnum,
        vararg validators: PropertyValidator<T, *>
    ) {
        val nonNullEntity = entity ?: error("entity not found")

        with(nonNullEntity) {
            val entityId = entityId ?: error("entityId is null")
            val entityClass = this::class

            // 1. Проверка полноты тестов (Expected vs Provided)
            val expectedFieldNames = fieldsCache.getOrPut(entityClass) {
                // Берем свойства из конструктора, так как они определяют состояние в БД
                entityClass.memberProperties.map { it.name }.toSet() - ignoredTechnicalFields
            }

            val providedFieldNames = validators.map { it.property.name }.toSet()

            val missingFields = expectedFieldNames - providedFieldNames
            require(missingFields.isEmpty()) {
                "Missing tests for fields in ${entityClass.simpleName}: $missingFields"
            }

            val unknownFields = providedFieldNames - expectedFieldNames
            require(unknownFields.isEmpty()) {
                "Unknown fields in validators for ${entityClass.simpleName}: $unknownFields"
            }

            // 2. Выполнение проверок
            validators.forEach { v ->
                val value = v.property.get(this)
                // Запускаем лямбду: 'this' будет сущностью, 'it' (первый аргумент) — значением поля
                (v as PropertyValidator<T, Any?>).assertion(this, value)
            }

            // 3. Проверка записи события в БД (core_actions)
            val count = databaseClient.sql(
                "SELECT count(*) as cnt FROM core_actions WHERE entity_id = :E AND action_code = :AC"
            )
                .bind("E", entityId)
                .bind("AC", actionEnum.actionCodeId)
                .map { row, _ -> row.get("cnt", Int::class.java) ?: 0 }
                .awaitOne()

            require(count > 0) {
                "Action record not found (entity: $entityId, action: $actionEnum)"
            }
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

@JvmInline
value class ErrorBox(val errors: Collection<ErrorInfo>) {

    fun shouldContainErrors(vararg expected: Pair<Error, Field>): ErrorBox = apply {
        expected.forEach { (error, field) ->
            withClue("Expected error ($error, $field) not found. Present errors: $errors") {
                errors.any { it.error == error && it.field == field } shouldBe true
            }
        }
    }
}
