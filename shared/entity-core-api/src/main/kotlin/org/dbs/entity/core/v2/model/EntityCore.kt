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
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
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
@Component
@Order(LOWEST_PRECEDENCE)
class EntityActionLoggerAspect(
    private val applicationEventPublisher: ApplicationEventPublisher
): Logging {

    private val actionCache = ConcurrentHashMap<String, Int>()

    @Around("@annotation(org.dbs.entity.core.v2.model.LogEntityAction)")
    fun logEntityAction(joinPoint: ProceedingJoinPoint): Any {
        val startTime = System.currentTimeMillis()
        val result = joinPoint.proceed()

        val method = (joinPoint.signature as MethodSignature).method

        val logEntityAction = method.getAnnotation(LogEntityAction::class.java)
            ?: error("Annotation @LogEntityAction not found on method ${method.name}")

        val eventAction: (EntityCore) -> Unit = { entity ->

            val actionCodeId = actionCache.getOrPut(logEntityAction.action) {
                entityActionEnums.firstOrNull { (it as Enum<*>).name == logEntityAction.action }?.actionCodeId
                    ?: error("Enum '${logEntityAction.action}' not found")
            }

            val duration = System.currentTimeMillis() - startTime
            val execInfo by lazy { "entity: ${result.javaClass}, method: ${method.name}, executed: $duration ms" }
            logger.info { execInfo }
            applicationEventPublisher.registryEntityEvent(
                entityId = entity.entityId ?: error("entityId must be set"),
                entityTypeId = entity.type.entityTypeId,
                actionCodeId = actionCodeId,
                "n/d",
                "...",
                duration = duration
            )

        }

        when (result)  {
            is Mono<*> -> {
                return result.doOnNext {
                    if (it is EntityCore) eventAction(it) else
                        error( "Unsupported Mono<type>: ${it::class.java.canonicalName} for @LogEntityAction" )
                }
            }
            is EntityCore -> {
                eventAction(result)
            }
            else -> {
                error( "Unsupported type: ${result::class.java.canonicalName} for @LogEntityAction" )
            }

        }
        return result

    }
}
