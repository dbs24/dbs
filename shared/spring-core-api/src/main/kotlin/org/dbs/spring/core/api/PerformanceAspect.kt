package org.dbs.spring.core.api

import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.CoroutinesUtils
import org.springframework.core.KotlinDetector
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TrackExecutionTime

@Aspect
@Component
@ConditionalOnProperty(prefix = "application.profiling", name = ["enabled"], havingValue = "true", matchIfMissing = false)
class PerformanceAspect : Logging {

    // Кэшируем только признак, является ли метод suspend, чтобы не хранить тяжелые объекты Method/Signature
    private val isSuspendCache = ConcurrentHashMap<Method, Boolean>()

    @Around("@annotation(org.dbs.spring.core.api.TrackExecutionTime)")
    fun logTime(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val methodName = signature.toShortString().replace("(..)", "")

        // Быстрая и атомарная проверка на suspend через официальный KotlinDetector
        val isSuspend = isSuspendCache.getOrPut(method) { KotlinDetector.isSuspendingFunction(method) }

        return if (isSuspend) {
            profileSuspend(joinPoint, method, methodName)
        } else {
            profileNormal(joinPoint, methodName)
        }
    }

    // Оптимизированный замер для suspend функций (через реактивный мост Spring)
    private fun profileSuspend(joinPoint: ProceedingJoinPoint, method: Method, methodName: String): Any {
        val startExec = System.currentTimeMillis()

        // Вызываем suspend функцию через утилиту Spring, которая преобразует её в Publisher
        val publisher = CoroutinesUtils.invokeSuspendingFunction(method, joinPoint.target, *joinPoint.args)

        // doOnSuccess сработает ВСЕГДА (и для Mono.empty(), и при наличии результата)
        return Mono.from(publisher)
            .doOnSuccess { entity ->
                val duration = System.currentTimeMillis() - startExec
                val resultClass = entity?.javaClass?.simpleName ?: "Void/Null"
                logger.info { "# $duration ms, $methodName, isSuspend: true, result: $resultClass" }
            }
            .doOnError { error ->
                val duration = System.currentTimeMillis() - startExec
                logger.error(error) { "# $duration ms [FAILED], $methodName, isSuspend: true" }
            }
    }

    // Оптимизированный замер для обычных синхронных процедур
    private fun profileNormal(joinPoint: ProceedingJoinPoint, methodName: String): Any? {
        val result: Any?
        val duration = measureTimeMillis {
            result = joinPoint.proceed()
        }

        // Если обычный метод вернул Mono (например, реактивный репозиторий в императивном коде)
        if (result is Mono<*>) {
            return result
                .doOnSuccess { entity ->
                    val resultClass = entity?.javaClass?.simpleName ?: "Void/Null"
                    logger.info { "# $duration ms (assembly), $methodName, isSuspend: false, result: $resultClass" }
                }
        }

        logger.info { "# $duration ms, $methodName, isSuspend: false" }
        return result
    }
}
