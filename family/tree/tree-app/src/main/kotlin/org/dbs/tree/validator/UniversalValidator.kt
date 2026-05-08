package org.dbs.tree.validator

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.validator.ValidationStrategy
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.exception.ValidationException
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
class UniversalValidator(
    val strategies: List<ValidationStrategy<*>>
) {
    private val strategyMap by lazy { strategies.associateBy { it.supportedClass } as Map<KClass<*>, ValidationStrategy<RequestDto>> }

    @Around("@annotation(validateDto)")
    suspend fun validateUserDto(joinPoint: ProceedingJoinPoint, validateDto: ValidateDto): Any? {

        val requestDto: RequestDto = joinPoint.args.find { arg ->
            strategyMap.keys.any { klass -> klass.isInstance(arg) }
        } as? RequestDto
            ?: throw ValidationException(
                listOf(
                    create(
                        Error.GENERAL_ERROR, Field.UNKNOWN_FIELD,
                        "No validatable DTO found in method arguments"
                    )
                )
            )

        requestDto.apply {
            val strategy = strategyMap[this::class]
                ?: throw ValidationException(
                    listOf(
                        create(
                            Error.GENERAL_ERROR, Field.UNKNOWN_FIELD,
                            "No validator found for ${requestDto::class.simpleName}"
                        )
                    )
                )
            strategy.validate(this as RequestDto)
        }
        return joinPoint.proceed()

    }
}


