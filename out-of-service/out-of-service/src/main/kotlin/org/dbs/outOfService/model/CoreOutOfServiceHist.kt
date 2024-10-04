package org.dbs.outOfService.model

import org.dbs.consts.EntityId
import org.dbs.consts.OperDateNull
import org.dbs.consts.StringNoteNull
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_out_of_service_hist")
data class CoreOutOfServiceHist(

    @Id
    val id: EntityId,

    @Column("actual_date")
    val actualDate: OperDateNull,

    @Column("service_date_start")
    val serviceDateStart: OperDateNull,

    @Column("service_date_finish")
    val serviceDateFinish: OperDateNull,

    @Column("note")
    val note: StringNoteNull

) : AbstractRefEntity<EntityId>() {
    override fun getId(): EntityId = id
}