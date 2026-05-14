package org.dbs.rest.validation

import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
import org.springframework.context.annotation.Lazy
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidateDto

@Aspect
@Component
@Order(HIGHEST_PRECEDENCE)
@Lazy(false)
class UniversalValidator(
    val strategies: List<ValidationStrategy<*>>
) : Logging {
    private val strategyMap by lazy { strategies.associateBy { it.supportedClass } as Map<KClass<*>, ValidationStrategy<DomainCommand>> }

    @Around("@annotation(org.dbs.rest.validation.ValidateDto)")
    fun validateDto(joinPoint: ProceedingJoinPoint): Any {

        val method = (joinPoint.signature as MethodSignature).method
        val isSuspend = method.parameterCount > 0 &&
                method.parameterTypes.last().name == "kotlin.coroutines.Continuation"
        val debugInfo by lazy {"method: ${method.name}, isSuspend: $isSuspend, parameters: ${method.parameterCount}"}

        val requestDto: DomainCommand = joinPoint.args.filterIsInstance<DomainCommand>().firstOrNull()
            ?: throw ValidationException(
                listOf(
                    create(
                        Error.GENERAL_ERROR, Field.UNKNOWN_FIELD,
                        "No validatable DTO found in method arguments ($debugInfo)"
                    )
                )
            )

        logger.debug { "validate dto: $requestDto ($debugInfo)" }

        requestDto.apply {
            val strategy = strategyMap[this::class]
                ?: throw ValidationException(
                    listOf(create(
                        Error.GENERAL_ERROR, Field.UNKNOWN_FIELD,
                        "No validator found for ${requestDto::class.simpleName} ($debugInfo)"
                    )
                    )
                )
            strategy.validate(this)

        }
        return joinPoint.proceed()
    }
}
