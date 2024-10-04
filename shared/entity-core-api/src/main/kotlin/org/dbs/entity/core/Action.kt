package org.dbs.entity.core


import org.dbs.consts.ActionId
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.StringNote
import org.dbs.entity.core.v2.consts.ActionCodeId
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalTime

@Table("core_Actions")
data class Action(
    @Id
    @Column("action_id")
    val actionId: ActionId,

    @Column("entity_id")
    val entityId: EntityId,

    @Column("user_id")
    val userId: EntityId,

    @Column("action_code")
    val actionCode: ActionCodeId,

    @Column("execute_date")
    val executeDate: OperDate,

    @Column("action_address")
    val actionAddress: String,

    @Column("err_msg")
    val errMsg: String,

    @Column("action_duration")
    val actionDuration: LocalTime,

    @Column("notes")
    val notes: StringNote
) : AbstractRefEntity<ActionId>() {

    //==================================================================================================================
    override fun getId() = actionId
}
