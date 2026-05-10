package org.dbs.ext

import org.dbs.consts.ActionCodeId
import org.dbs.consts.EntityId
import org.dbs.consts.EntityTypeId
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.Field
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

    fun String.fromErrString(): ErrorInfo {
//        val (error, field, errorMsg) = split(": ", limit = 3)
//        return ErrorInfo(Error.valueOf(error), Field.valueOf(field), errorMsg)
        val parts = this.split(": ", limit = 3)
        return ErrorInfo(
            error = Error.valueOf(parts[0]),
            field = Field.valueOf(parts[1]),
            errorMsg = parts.getOrNull(2) ?: ""
        )
    }

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