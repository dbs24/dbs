package org.dbs.spring.core.api

import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TrackExecutionTime

@Aspect
@Component
@ConditionalOnProperty(
    prefix = "application.profiling",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class PerformanceAspect : Logging {

    @Pointcut("@annotation(org.dbs.spring.core.api.TrackExecutionTime)")
    fun hasTrackAnnotation() {
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    fun isRestController() {
    }

    @Pointcut("@within(org.springframework.grpc.server.service.GrpcService)")
    fun isGrpcService() {
    }

    @Around("hasTrackAnnotation() || isRestController() || isGrpcService()")
    fun logTime(joinPoint: ProceedingJoinPoint): Any {
        val signature = joinPoint.signature as MethodSignature
        val methodName = signature.toShortString().replace("(..)", "")

        return Mono.defer {

            val startExec = System.currentTimeMillis()
            val duration: () -> Long = { System.currentTimeMillis() - startExec }

            (joinPoint.proceed() as Mono<*>).doOnSuccess { entity ->
                val resultClass = entity?.javaClass?.simpleName ?: "Void/Null"
                val duration = duration()
                val msg = { "# $duration ms, $methodName, result: $resultClass" }
                if (duration > 1000)
                    logger.warn { msg() }
                else
                    logger.info { msg() }
            }
                .doOnError { error ->
                    val errMsg = error.message ?: error.toString()
                    logger.error { "# ${duration()} ms [FAILED]: $errMsg }, $methodName" }
                }
        }
    }
}
