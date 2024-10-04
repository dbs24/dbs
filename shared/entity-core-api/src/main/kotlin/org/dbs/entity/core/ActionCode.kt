package org.dbs.entity.core

import org.dbs.spring.ref.AbstractRefEntity
import org.dbs.consts.ClosedStatus
import org.dbs.entity.core.v2.consts.ActionCodeId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_action_codes_ref")
data class ActionCode(
    @Id
    @Column("action_code")
    val actionCode: ActionCodeId,

    @Column("action_name")
    val actionName: String,

    @Column("app_name")
    val appName: String,

    @Column("is_closed")
    val isClosed: ClosedStatus

) : AbstractRefEntity<ActionCodeId>() {
    override fun getId(): ActionCodeId = actionCode
}
