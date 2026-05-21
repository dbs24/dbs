package org.dbs.service.aop

import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.entity.core.v2.model.LogEntityAction
import org.dbs.ext.SpringFuncs.registryEntityEvent
import org.dbs.service.sync.CoreEnumsSynchronizer
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
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

@Aspect
@Lazy(false)
@Component
@Order(LOWEST_PRECEDENCE)
class EntityActionLoggerAspect(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val applicationContext: ApplicationContext,
    private val coreEnumsSynchronizer: CoreEnumsSynchronizer
) : Logging {

    // Кеш сигнатур методов → ускоряет AOP ×3–×5
    private val methodCache = ConcurrentHashMap<Signature, Method>()

    // Кеш actionCodeId → исключает поиск по enum
    private val actionCache = ConcurrentHashMap<String, Int>()

    // Кэшируем имена ENUM в HashSet для мгновенного поиска O(1) как при старте, так и в рантайме
    private val validActionNames by lazy {
        coreEnumsSynchronizer.metadata.actions.mapTo(HashSet(coreEnumsSynchronizer.metadata.actions.size))
        { (it as Enum<*>).name }
    }

    @Around("@annotation(org.dbs.entity.core.v2.model.LogEntityAction)")
    fun logEntityAction(joinPoint: ProceedingJoinPoint): Any {

        val method = methodCache.getOrPut(joinPoint.signature) {
            (joinPoint.signature as MethodSignature).method
        }

        val annotation = method.getAnnotation(LogEntityAction::class.java)
            ?: error("Annotation @LogEntityAction not found on ${method.name}")

        val actionCodeId = actionCache.getOrPut(annotation.action) {
            coreEnumsSynchronizer.metadata.actions.firstOrNull { (it as Enum<*>).name == annotation.action }?.actionCodeId
                ?: error("Enum '${annotation.action}' not found")
        }

        val startTime = System.currentTimeMillis()
        val result = joinPoint.proceed()

        return when (result) {

            // --- Реактивный Mono ---
            is Mono<*> -> {
                Mono.defer {
                    val reactiveStart = System.currentTimeMillis()
                    result.doOnNext { entity ->
                        if (entity is EntityCore) {
                            val duration = System.currentTimeMillis() - reactiveStart
                            publish(entity, actionCodeId, method, duration)
                        } else {
                            error("Unsupported Mono<type>: ${entity::class.java.canonicalName}")
                        }
                    }
                }
            }

            // --- Синхронный EntityCore ---
            is EntityCore -> {
                val duration = System.currentTimeMillis() - startTime
                publish(result, actionCodeId, method, duration)
                result
            }

            else -> error("Unsupported return type: ${result?.javaClass?.canonicalName}")
        }
    }

    private fun publish(entity: EntityCore, actionCodeId: Int, method: Method, duration: Long) {
        val entityId = entity.entityId ?: error("entityId must be set")

        logger.info {
            "entity=${entity::class.qualifiedName}, method=${method.name}, duration=${duration}ms"
        }

        applicationEventPublisher.registryEntityEvent(
            entityId = entityId,
            entityTypeId = entity.type.entityTypeId,
            actionCodeId = actionCodeId,
            remoteAddr = "n/d",
            actionNote = "...",
            duration = duration
        )
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