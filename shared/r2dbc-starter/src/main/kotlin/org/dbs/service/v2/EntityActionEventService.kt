package org.dbs.service.v2

import com.sun.management.UnixOperatingSystemMXBean
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.entity.core.EntityAction
import org.dbs.entity.core.Incident
import org.dbs.ext.EntityActionEvent
import org.dbs.ext.IncidentEvent
import org.dbs.service.repo.ActionRepo
import org.dbs.service.repo.IncidentRepo
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.lang.management.ManagementFactory
import java.time.LocalDateTime.now
import java.time.LocalTime.MIN

@Service
class EntityActionEventService(
    private val actionRepo: ActionRepo,
    private val incidentRepo: IncidentRepo,
) : Logging {

    @EventListener
    @Async
    fun onActionEvent(event: EntityActionEvent): Unit = runBlocking {
        with(event) {
            actionRepo.save(
                EntityAction(
                    actionId = null,
                    entityId = entityId,
                    entityTypeId = entityTypeId,
                    userId = userId,
                    actionCode = actionCodeId,
                    executeDate = now(),
                    actionAddress = remoteAddr,
                    errMsg = STRING_NULL,
                    actionDuration = MIN,
                    notes = actionNote,
                ).asNew()
            )
            logger.debug { "Action registered: entityId=$entityId, entityTypeId=$entityTypeId, actionCode=$actionCodeId" }
        }
    }

    @EventListener
    @Async
    fun onIncidentEvent(event: IncidentEvent): Unit = runBlocking {
        with(event) {

            val osBean = ManagementFactory.getOperatingSystemMXBean() as? UnixOperatingSystemMXBean
            val runtime = Runtime.getRuntime()

            incidentRepo.save(
                Incident(
                    incidentId = incidentId,
                    source = source.toString(),
                    path = path,
                    stackTrace = stackTrace,
                    osOpenFiles = osBean?.openFileDescriptorCount ?: -1L,
                    jvmFreeMemoryBytes = runtime.freeMemory(),
                    jvmTotalMemoryBytes = runtime.totalMemory(),
                    jvmMaxMemoryBytes = runtime.maxMemory(),
                )
            ).also {
                logger.error { "Incident registered: $it" }
            }
        }
    }
}
