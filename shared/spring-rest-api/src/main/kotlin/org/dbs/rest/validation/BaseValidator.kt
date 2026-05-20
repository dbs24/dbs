package org.dbs.rest.validation

import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.Signature
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.spring.core.api.ServiceLocator.findBeanInstance
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.getOrPut
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidateDto

@Aspect
@Component
@Order(HIGHEST_PRECEDENCE)
@Lazy(false)
class UniversalValidator(
    val strategies: List<ValidationStrategy<*>>,
    private val applicationContext: ApplicationContext
) : Logging {
    // Кеш сигнатур методов → ускоряет AOP ×3–×5
    private val methodCache = ConcurrentHashMap<Signature, Method>()

    // Мапа стратегий по точному KClass
    private val strategyMap: Map<KClass<*>, ValidationStrategy<DomainCommand>> by lazy {
        strategies.associateBy { it.supportedClass } as Map<KClass<*>, ValidationStrategy<DomainCommand>> }

    // Кеш найденных стратегий с учётом наследования DTO
    private val strategyCache by lazy { ConcurrentHashMap<KClass<*>, ValidationStrategy<DomainCommand>>() }

    private fun findStrategy(dto: DomainCommand): ValidationStrategy<DomainCommand> =
        strategyCache.getOrPut(dto::class) {
            strategyMap[dto::class]
                ?: strategyMap.entries.firstOrNull { (k, _) -> k.isInstance(dto) }?.value
                ?: throw ValidationException(
                    listOf(
                        create(
                            Error.GENERAL_ERROR,
                            Field.UNKNOWN_FIELD,
                            "No validator found for ${dto::class.simpleName}"
                        )
                    )
                )
        }

    @Around("@annotation(org.dbs.rest.validation.ValidateDto)")
    fun validateDto(joinPoint: ProceedingJoinPoint): Any {

        val method = methodCache.getOrPut(joinPoint.signature) {
            (joinPoint.signature as MethodSignature).method
        }

        val isSuspend = method.parameterTypes.lastOrNull()?.name == "kotlin.coroutines.Continuation"
        val debugInfo = "method: ${method.name}, isSuspend: $isSuspend, parameters: ${method.parameterCount}"

        val requestDto = joinPoint.args.firstOrNull { it is DomainCommand } as? DomainCommand
            ?: throw ValidationException(
                listOf(
                    create(
                        Error.GENERAL_ERROR,
                        Field.UNKNOWN_FIELD,
                        "No validatable DTO found in method arguments ($debugInfo)"
                    )
                )
            )

        logger.debug { "validate dto: $requestDto ($debugInfo)" }

        val strategy = findStrategy(requestDto)
        strategy.validate(requestDto)

        return joinPoint.proceed()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun validateAnnotations() {
        val beanNames = applicationContext.beanDefinitionNames
        val errors = mutableListOf<String>()

        for (beanName in beanNames) {
            // Пропускаем встроенные бины Spring сразу по имени (экономит время на получение инстанса)
            if (beanName.startsWith("org.springframework")) continue

            val beanInstance = applicationContext.findBeanInstance(beanName) ?: continue
            val targetClass = ClassUtils.getUserClass(beanInstance)

            // Пропускаем сами классы Spring и сторонних библиотек
            val className = targetClass.name
            if (className.startsWith("org.springframework") || className.startsWith("java.")) continue

            for (method in targetClass.declaredMethods) {
                // Быстрая проверка аннотации без создания тяжелых объектов MergedAnnotations
                val annotation = method.getAnnotation(ValidateDto::class.java) ?: continue

                // Ищем параметр-команду
                var commandParamType: Class<*>? = null
                for (i in 0 until method.parameterCount) {
                    val pType = method.parameterTypes[i]
                    if (DomainCommand::class.java.isAssignableFrom(pType)) {
                        commandParamType = pType
                        break
                    }
                }

                if (commandParamType != null) {
                    val kotlinClass = commandParamType.kotlin
                    if (!strategyMap.containsKey(kotlinClass)) {
                        // Строку debugInfo формируем ТОЛЬКО в момент обнаружения ошибки
                        errors.add("$kotlinClass has no validation strategy (${createDebugInfo(targetClass, method)})")
                    }
                } else {
                    errors.add("No validatable DTO found in method arguments (${createDebugInfo(targetClass, method)})")
                }
            }
        }

        if (errors.isNotEmpty()) {
            error("Validation failed for @ValidateDto annotations:\n" + errors.joinToString("\n"))
        }
    }

    // Вынесено в отдельную функцию, чтобы не создавать объекты в цикле
    private fun createDebugInfo(targetClass: Class<*>, method: java.lang.reflect.Method): String {
        val isSuspend = method.parameterCount > 0 &&
                method.parameterTypes.last().name == "kotlin.coroutines.Continuation"
        return "method: ${targetClass.name}.${method.name}, isSuspend: $isSuspend, parameters: ${method.parameterCount}"
    }

}
