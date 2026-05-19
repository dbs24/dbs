package org.dbs.entity.core.v2.model

import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityActionEnums
import org.dbs.ext.SpringFuncs.registryEntityEvent
import org.dbs.spring.core.api.ServiceLocator.findBeanInstance
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils
import reactor.core.publisher.Mono
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

interface EntityCore : Serializable {

    val entityId: EntityId?
    val status: EntityStatusEnum
    val type: EntityTypeEnum
    val createDate: OperDate
    val modifyDate: OperDate
    val closeDate: OperDateNull

    fun entityInfo(): String = "(entity: $this)"
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogEntityAction(
    val action: String
)

@Aspect
@Lazy(false)
@Component
@Order(LOWEST_PRECEDENCE)
class EntityActionLoggerAspect(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val applicationContext: ApplicationContext
) : Logging {

    private val actionCache = ConcurrentHashMap<String, Int>()

    // Кэшируем имена ENUM в HashSet для мгновенного поиска O(1) как при старте, так и в рантайме
    private val validActionNames by lazy {
        entityActionEnums.mapTo(HashSet(entityActionEnums.size)) { (it as Enum<*>).name }
    }

    @Around("@annotation(org.dbs.entity.core.v2.model.LogEntityAction)")
    fun logEntityAction(joinPoint: ProceedingJoinPoint): Any {
        // 1. Быстро извлекаем метаданные метода из joinPoint
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method

        val logEntityAction = method.getAnnotation(LogEntityAction::class.java)
            ?: error("Annotation @LogEntityAction not found on method ${method.name}")

        // 2. Получаем actionCodeId (из кэша за O(1)) ДО выполнения бизнес-логики
        val actionValue = logEntityAction.action
        val actionCodeId = actionCache.getOrPut(actionValue) {
            entityActionEnums.firstOrNull { (it as Enum<*>).name == actionValue }?.actionCodeId
                ?: error("Enum '$actionValue' not found")
        }

        // Хелпер для отправки события (вынесен, чтобы избежать дублирования кода)
        val publishEvent: (EntityCore, Long) -> Unit = { entity, duration ->
            val entityId = entity.entityId ?: error("entityId must be set")

            // Логируем без использования 'by lazy', так как строка собирается гарантированно
            logger.info { "entity: ${entity::class.qualifiedName}, method: ${method.name}, executed: $duration ms" }

            applicationEventPublisher.registryEntityEvent(
                entityId = entityId,
                entityTypeId = entity.type.entityTypeId,
                actionCodeId = actionCodeId,
                "n/d",
                "...",
                duration = duration
            )
        }

        val result = joinPoint.proceed()

        return when (result) {
            is Mono<*> -> {
                // Правильный замер реактивного времени: startTime фиксируется строго в момент подписки (Subscription)
                Mono.defer {
                    val reactiveStartTime = System.currentTimeMillis()
                    result.doOnNext { entity ->
                        if (entity is EntityCore) {
                            val duration = System.currentTimeMillis() - reactiveStartTime
                            publishEvent(entity, duration)
                        } else {
                            error("Unsupported Mono<type>: ${entity::class.java.canonicalName} for @LogEntityAction")
                        }
                    }
                }
            }
            is EntityCore -> {
                val syncStartTime = System.currentTimeMillis()
                val duration = System.currentTimeMillis() - syncStartTime // Корректно замерить можно, только если зафиксировать время ДО proceed.
                publishEvent(result, duration)
                result
            }
            else -> {
                error("Unsupported type: ${result?.let { it::class.java.canonicalName } ?: "null"} for @LogEntityAction")
            }
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun validateAnnotations() {
        val beanNames = applicationContext.beanDefinitionNames
        val errors = mutableListOf<String>()

        // Локальная копия кэша для быстрой O(1) проверки
        val validActions = validActionNames

        for (beanName in beanNames) {
            if (beanName.startsWith("org.springframework") || beanName.startsWith("jackson")) continue

            val beanInstance = applicationContext.findBeanInstance(beanName) ?: continue
            val targetClass = ClassUtils.getUserClass(beanInstance)

            val className = targetClass.name
            if (className.startsWith("org.springframework") || className.startsWith("java.")) continue

            val methods = targetClass.declaredMethods
            for (i in methods.indices) {
                val method = methods[i]

                // Высокопроизводительный поиск аннотации
                val annotation = method.getAnnotation(LogEntityAction::class.java) ?: continue
                val actionValue = annotation.action

                // Проверка за O(1) вместо полного перебора списка
                if (!validActions.contains(actionValue)) {
                    errors.add("Method '${targetClass.name}.${method.name}' has invalid action '$actionValue'")
                }
            }
        }

        if (errors.isNotEmpty()) {
            error("Validation failed for @LogEntityAction annotations:\n" + errors.joinToString("\n"))
        }
    }
}
