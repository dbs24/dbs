package org.dbs.entity.core.v2.type

import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.entity.core.v2.consts.ActionName
import org.dbs.entity.core.v2.consts.EntityTypeId

abstract class AbstractActionCode(
    override val actionCodeId: ActionCodeId,
    override val entityTypeId: EntityTypeId,
    override val actionName: ActionName,
) : ActionCode {
    override fun toString() =
        let { "ActionCode(actonCodeId=$actionCodeId;entityTypeId=$entityTypeId;actionName=$actionName;)" }

}
