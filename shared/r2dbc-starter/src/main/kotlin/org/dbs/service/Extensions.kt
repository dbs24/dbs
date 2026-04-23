package org.dbs.service

import org.dbs.consts.EntityId
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.service.v2.ActionEvent
import org.springframework.context.ApplicationEventPublisher

object Extensions {

    fun ApplicationEventPublisher.registryEvent(
        entityId : EntityId,
        entityTypeId: EntityTypeId,
        actionCodeId: ActionCodeId,
        remoteAddr: IpAddress,
        actionNote: StringNote,
        userId: EntityId = 1L) =

        publishEvent(
            ActionEvent(
                entityId = entityId,
                entityTypeId = entityTypeId,
                actionCodeId = actionCodeId,
                remoteAddr = remoteAddr,
                actionNote = actionNote,
                userId = userId
            )
        )
}