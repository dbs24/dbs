package org.dbs.ext

import org.dbs.consts.ActionCodeId
import org.dbs.consts.EntityId
import org.dbs.consts.EntityTypeId
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.springframework.context.ApplicationEventPublisher

object SpringFuncs {

    fun ApplicationEventPublisher.registryEvent(
        entityId : EntityId,
        entityTypeId: EntityTypeId,
        actionCodeId: ActionCodeId,
        remoteAddr: IpAddress,
        actionNote: StringNote,
        duration: Long = -1L,
        userId: EntityId = 1L): Unit =

        publishEvent(
            ActionEvent(
                entityId = entityId,
                entityTypeId = entityTypeId,
                actionCodeId = actionCodeId,
                remoteAddr = remoteAddr,
                actionNote = actionNote,
                duration = duration,
                userId = userId
            )
        )

}

data class ActionEvent(
    val entityId: EntityId,
    val entityTypeId: EntityTypeId,
    val actionCodeId: ActionCodeId,
    val remoteAddr: IpAddress,
    val actionNote: StringNote,
    var duration: Long = -1,
    val userId: EntityId = 1L,
)