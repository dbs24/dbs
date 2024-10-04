package org.dbs.entity.core.v2.model


import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_entities")
data class Entity(
    @Id @Column("entity_id")
    val entityId: EntityId,

    @Column("entity_type_id")
    val entityType: EntityTypeEnum,

    @Column("entity_status_id")
    val entityStatus: EntityStatusEnum,

    @Column("create_date")
    val createDate: OperDate,

    @Column("modify_date")
    val modifyDate: OperDate,

    @Column("close_date")
    val closeDate: OperDateNull = null
)  : AbstractRefEntity<EntityId>() {
    override fun getId() = entityId
}
