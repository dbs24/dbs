package org.dbs.service.v2

import org.dbs.consts.EntityId
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.entity.core.v2.consts.EntityTypeId

data class ActionEvent(
    val entityId: EntityId,
    val entityTypeId: EntityTypeId,
    val actionCodeId: ActionCodeId,
    val remoteAddr: IpAddress,
    val actionNote: StringNote,
    val userId: EntityId = 1L,
)
