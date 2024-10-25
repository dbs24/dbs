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
    @Id
    val entityId: EntityId,
    @Column("entity_type_id")
    val entityType: EntityTypeEnum,
    @Column("entity_status_id")
    val entityStatus: EntityStatusEnum,
    val createDate: OperDate,
    val modifyDate: OperDate,
    val closeDate: OperDateNull = null
)  : AbstractRefEntity<EntityId>() {
    override fun getId() = entityId
}
