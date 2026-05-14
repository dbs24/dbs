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
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicLong

object SpringFuncs {

    val sequence = AtomicLong(0L)

    fun generateIncidentIdAndMsg(e: Throwable): Triple<String, String, String> =
        (System.currentTimeMillis().toString()+"_"+ sequence.getAndIncrement())
        .let {
            e.printStackTrace()
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            Triple(it, "An unexpected error occurred. Please contact support with incident ID: $it", sw.toString())
        }

    fun ApplicationEventPublisher.registryEntityEvent(
        entityId : EntityId,
        entityTypeId: EntityTypeId,
        actionCodeId: ActionCodeId,
        remoteAddr: IpAddress,
        actionNote: StringNote,
        duration: Long = -1L,
        userId: EntityId = 1L): Unit =

        publishEvent(
            EntityActionEvent(
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

    fun ApplicationEventPublisher.registryIncidentEvent(
        throwable: Throwable,
        source: IncidentSource): String {

        val (incidentId, incidentMsg, stackTrace) = generateIncidentIdAndMsg(throwable)

        publishEvent(
            IncidentEvent(
                incidentId = incidentId,
                source = source,
                message = throwable.message ?: incidentMsg,
                stackTrace = stackTrace
            )
        )

        return incidentMsg
    }
}

data class EntityActionEvent(
    val entityId: EntityId,
    val entityTypeId: EntityTypeId,
    val actionCodeId: ActionCodeId,
    val remoteAddr: IpAddress,
    val actionNote: StringNote,
    var duration: Long = -1,
    val userId: EntityId = 1L,
)

data class IncidentEvent(
    val incidentId: String,
    val source: IncidentSource,
    val message: String,
    val stackTrace: String
)

enum class IncidentSource {
    IS_REST,
    IS_GRPC
}