package org.dbs.test.ko

import api.TestConst.SQL_TEST_DB_NAME
import api.TestConst.SQL_TEST_DB_USER
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
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
import org.dbs.test.container.KafkaTestContainer
import org.dbs.test.container.PostgresR2dbcContainer
import org.dbs.test.container.RedisTestContainer
import org.dbs.test.core.SysTestConsts.Grpc.GRPC_RANDOM_SERVER_PORT
import org.dbs.test.core.SysTestConsts.Postgres.TEST_PG_R2DBC_IMAGE_TAG
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.Field
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@Testcontainers
@ContextConfiguration
@TestMethodOrder(OrderAnnotation::class)
@TestPropertySource(
    properties = [
        "grpc.server.port=0", "grpc.server.security.enabled=false", "$JUNIT_MODE=true", "$KOTEST_MODE=true",
        "$SERVER_SSL_ENABLED=$SERVER_SSL_DISABLED", "$GRPC_SERVER_PORT=$GRPC_RANDOM_SERVER_PORT",
        "$YML_CORS_CONFIG_ENABLED=$STRING_FALSE", "$SPRINGDOC_API_DOCS_ENABLED=$STRING_FALSE",
        "$SPRINGDOC_SWAGGER_ENABLED=$STRING_FALSE", "$BUCKET_4J_ENABLED=$STRING_FALSE",
        "$REFERENCES_AUTO_SYNCHRONIZE=$STRING_TRUE", "$JWT_SECRET_KEY=$JWT_SECRET_KEY_VALUE",
        "$SPRING_R2DBC_URL=$TEST_PG_R2DBC_IMAGE_TAG", "$DEFAULT_SYS_CURRENCY=$DEFAULT_SYS_CURRENCY_VALUE"
    ]
)
abstract class BaseSpec : StringSpec(), Logging {

    @Autowired
    lateinit var databaseClient: DatabaseClient

    override val extensions = listOf(SpringExtension)
    abstract val source: String

    private val fieldsCache = ConcurrentHashMap<KClass<*>, Set<String>>()
    private val ignoredTechnicalFields = setOf("type", "status")

    class PropertyValidator<T, V>(val property: KProperty1<T, V>, val assertion: T.(V) -> Unit)

    infix fun <T, V> KProperty1<T, V>.verify(assertion: T.(V) -> Unit) = PropertyValidator(this, assertion)

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> validateEntityFields(
        entity: T,
        verifyAllFields: Boolean,
        validators: Array<out PropertyValidator<T, *>>
    ) {
        val entityClass = entity::class
        logger.info { "Verify entity fields for: $entityClass" }

        if (verifyAllFields) {
            val expectedFieldNames = fieldsCache.getOrPut(entityClass) {
                entityClass.memberProperties.mapTo(HashSet()) { it.name } - ignoredTechnicalFields
            }
            val providedFieldNames = validators.mapTo(HashSet(validators.size)) { it.property.name }

            if (expectedFieldNames.size != providedFieldNames.size || !expectedFieldNames.containsAll(providedFieldNames)) {
                val missingFields = expectedFieldNames - providedFieldNames
                require(missingFields.isEmpty()) { "Missing tests for fields in ${entityClass.simpleName}: $missingFields" }
                val unknownFields = providedFieldNames - expectedFieldNames
                require(unknownFields.isEmpty()) { "Unknown fields in validators for ${entityClass.simpleName}: $unknownFields" }
            }
        }

        val seenProperties = HashSet<KProperty1<T, *>>(validators.size)
        validators.forEach { v ->
            require(seenProperties.add(v.property)) {
                "Duplicate validators found: Field '${v.property.name}' of '${entityClass.qualifiedName}' appears multiple times"
            }
            (v as PropertyValidator<T, Any?>).assertion(entity, v.property.get(entity))
        }
    }

    fun <T : Any> verifyModifiedEntity(
        entity: T?,
        verifyAllFields: Boolean = true,
        vararg validators: PropertyValidator<T, *>,
    ): T {
        requireNotNull(entity) { "entity not found" }
        validateEntityFields(entity, verifyAllFields, validators)
        return entity
    }

    suspend fun <T : EntityCore> verifyModifiedEntity(
        entity: T?,
        actionEnum: EntityActionEnum,
        verifyAllFields: Boolean = true,
        vararg validators: PropertyValidator<T, *>,
    ): T {
        requireNotNull(entity) { "entity not found" }
        val entityId = requireNotNull(entity.entityId) { "entityId is null" }

        logger.info { "Verify entity actions: $entityId (${entity::class.simpleName})" }

        // Переиспользуем общий метод валидации полей
        validateEntityFields(entity, verifyAllFields, validators)

        val hasAction =
            databaseClient.sql("SELECT EXISTS(SELECT 1 FROM core_actions WHERE entity_id = :E AND action_code = :AC LIMIT 1)")
                .bind("E", entityId)
                .bind("AC", actionEnum.actionCodeId)
                .map { row, _ -> row.get(0, java.lang.Boolean::class.java)?.booleanValue() ?: false }
                .awaitOne()

        require(hasAction) { "Action record not found (entity: $entityId, action: $actionEnum)" }

        return entity
    }

    companion object {
        private val atomicTestNum = AtomicInteger(0)

        @JvmStatic
        protected val testNum: Int get() = atomicTestNum.incrementAndGet()

        val postgresR2dbcContainer = PostgresR2dbcContainer(SQL_TEST_DB_NAME, SQL_TEST_DB_USER)
        private val kafkaTestContainer = KafkaTestContainer()
        private val redisTestContainer = RedisTestContainer()

        @JvmStatic
        @DynamicPropertySource
        @Suppress("unused")
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
                var found = false
                for (err in errors) {
                    if (err.error == error && err.field == field) { found = true; break }
                }
                found shouldBe true
            }
        }
    }
}
