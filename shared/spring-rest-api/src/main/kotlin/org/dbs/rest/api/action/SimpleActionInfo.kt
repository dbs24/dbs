package org.dbs.rest.api.action

import org.dbs.spring.core.api.FlatEntityAction
import org.dbs.rest.api.action.RestAction.NO_ACTION


class SimpleActionInfo : FlatEntityAction {
    private var actionCode: RestAction = NO_ACTION

    companion object {
        fun createSimpleActionInfo(actionCode: RestAction) = SimpleActionInfo().also {
            it.actionCode = actionCode
        }
    }

    override fun toString() = "ActionInfo(${actionCode.getCode()} - ${actionCode.getValue()})"

}
