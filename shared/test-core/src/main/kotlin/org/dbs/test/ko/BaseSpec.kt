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
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
typealias FieldValidator<T> = Pair<String, T.() -> Unit>

@Testcontainers
@ContextConfiguration
@TestMethodOrder(OrderAnnotation::class)
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
abstract class BaseSpec: StringSpec(), Logging {

    @Autowired
    lateinit var databaseClient: DatabaseClient

    override val extensions = listOf(SpringExtension)

    abstract val source: String

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
        verifyAllFields: Boolean = true,
        vararg validators: PropertyValidator<T, *>,
    ) {
        val nonNullEntity = entity ?: error("entity not found")

        with(nonNullEntity) {
            val entityId = entityId ?: error("entityId is null")
            val entityClass = this::class

            logger.info { "Verify entity: $entityId ($entityClass)" }

            if (verifyAllFields) {
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
            }

            val duplicates = validators
                .groupBy { it.property }
                .filter { it.value.size > 1 }

            require (duplicates.isEmpty()) {
                "Duplicate validators found: ${duplicates.entries.joinToString(separator = "; ") { (property, list) ->
                    "Field '${property.name}' of '${entity.let { it::class.qualifiedName }}' appears ${list.size} times"}}"
            }

            validators.forEach { v ->
                val value = v.property.get(this)
                // Запускаем лямбду: 'this' будет сущностью, 'it' (первый аргумент) — значением поля
                (v as PropertyValidator<T, Any?>).assertion(this, value)
            }

            // 3. Проверка записи события в БД (core_actions)
            val count = databaseClient.sql(
                """
                    SELECT CASE 
                        WHEN EXISTS (
                            SELECT 1 
                            FROM core_actions 
                            WHERE entity_id = :E AND action_code = :AC
                        ) THEN 1 
                        ELSE 0 
                    END as cnt                    
                """
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

    inline infix fun <V> V.verifyThat(crossinline assertion: V.() -> Unit) {
        this.assertion()
    }

    // 2й способ верификации entity без рефлексии (KProperty1)
    suspend fun <T : EntityCore> verifyModifiedEntity2(
        entity: T?,
        actionEnum: EntityActionEnum,
        verifyAllFields: Boolean = true,
        vararg validators: FieldValidator<T>,
    ) {
        val nonNullEntity = entity ?: error("entity not found")

        with(nonNullEntity) {
            val entityId = entityId ?: error("entityId is null")
            val entityClass = this::class

            logger.info { "Verify entity: $entityId ($entityClass)" }

            if (verifyAllFields) {
                // Извлекаем имена из кэша по ключу KClass
                val expectedFieldNames = fieldsCache.getOrPut(entityClass) {
                    // entityClass.java возвращает java.lang.Class, у которого гарантированно есть declaredFields
                    entityClass.java.declaredFields.map { it.name }.toSet() - ignoredTechnicalFields
                }

                val providedFieldNames = validators.map { it.first }.toSet()

                val missingFields = expectedFieldNames - providedFieldNames
                require(missingFields.isEmpty()) {
                    "Missing tests for fields in ${entityClass.simpleName}: $missingFields"
                }

                val unknownFields = providedFieldNames - expectedFieldNames
                require(unknownFields.isEmpty()) {
                    "Unknown fields in validators for ${entityClass.simpleName}: $unknownFields"
                }
            }

            // Проверка на дубликаты
            val duplicates = validators.groupBy { it.first }.filter { it.value.size > 1 }
            require(duplicates.isEmpty()) { "Duplicate validators found for fields: ${duplicates.keys}" }

            // Запуск проверок в контексте сущности
            validators.forEach { (_, assertionBlock) ->
                nonNullEntity.assertionBlock()
            }


            val count = databaseClient.sql(
                """
                    SELECT CASE
                        WHEN EXISTS (
                            SELECT 1
                            FROM core_actions
                            WHERE entity_id = :E AND action_code = :AC
                        ) THEN 1
                        ELSE 0
                    END as cnt
                """
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


    companion object {

        val postgresR2dbcContainer = PostgresR2dbcContainer(SQL_TEST_DB_NAME, SQL_TEST_DB_USER)
        private val kafkaTestContainer = KafkaTestContainer()
        private val redisTestContainer = RedisTestContainer()

        @JvmStatic
        @Suppress("unused")
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
