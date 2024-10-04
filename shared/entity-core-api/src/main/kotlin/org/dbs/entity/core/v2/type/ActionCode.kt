package org.dbs.entity.core.v2.type

import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.entity.core.v2.consts.ActionName
import org.dbs.entity.core.v2.consts.EntityTypeId

sealed interface ActionCode {
    val actionCodeId: ActionCodeId
    val entityTypeId: EntityTypeId
    val actionName: ActionName
}
