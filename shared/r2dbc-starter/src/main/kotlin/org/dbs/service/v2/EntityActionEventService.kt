package org.dbs.service.v2

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.entity.core.Action
import org.dbs.ext.ActionEvent
import org.dbs.service.repo.ActionRepo
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.time.LocalTime.MIN

@Service
class EntityActionEventService(
    private val actionRepo: ActionRepo,
) : Logging {

    @EventListener
    @Async
    fun onActionEvent(event: ActionEvent): Unit = runBlocking {
        with(event) {
            actionRepo.save(
                Action(
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
}
