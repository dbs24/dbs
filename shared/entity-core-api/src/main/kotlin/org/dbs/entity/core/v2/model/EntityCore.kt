package org.dbs.entity.core.v2.model

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.type.EntityCoreInitializer.Companion.EntityCore.entityActionEnums
import org.dbs.ext.SpringFuncs.registryEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
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
) {

    private val actionCache = ConcurrentHashMap<String, Int>()

    @Around("@annotation(logEntityAction)")
    suspend fun logEntityAction(joinPoint: ProceedingJoinPoint, logEntityAction: LogEntityAction): Any? {
        val startTime = System.currentTimeMillis()
        val result = joinPoint.proceed()
        val duration = System.currentTimeMillis() - startTime

        if (result is EntityCore) {

            val actionCodeId = actionCache.getOrPut(logEntityAction.action) {
                entityActionEnums.firstOrNull { (it as Enum<*>).name == logEntityAction.action }?.actionCodeId
                    ?: error("Enum '${logEntityAction.action}' not found")
            }

            applicationEventPublisher.registryEvent(
                entityId = result.entityId ?: error("entityId must be set"),
                entityTypeId = result.type.entityTypeId,
                actionCodeId = actionCodeId,
                "n/d",
                "...",
                duration = duration
            )
        }
        return result
    }
}
