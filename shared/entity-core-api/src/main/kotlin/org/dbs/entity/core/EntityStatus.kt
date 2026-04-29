package org.dbs.entity.core


import org.dbs.consts.EntityStatusId
import org.dbs.consts.EntityTypeId
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_entity_statuses_ref")
data class EntityStatus(
    @Id
    @Column("entity_status_id")
    val entityStatus: EntityStatusId,

    @Column("entity_type_id")
    val entityType: EntityTypeId,

    @Column("entity_status_name")
    val entityStatusName: String
) : AbstractRefEntity<EntityStatusId>() {
    //==================================================================================================================
    override fun getId() = entityStatus
}
