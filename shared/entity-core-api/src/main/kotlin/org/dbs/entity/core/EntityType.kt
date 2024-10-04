package org.dbs.entity.core

import org.dbs.entity.core.v2.consts.EntityTypeId
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_entity_types_ref")
data class EntityType(
    @Id
    @Column("entity_type_id")
    val entityTypeId: EntityTypeId,

    @Column("entity_type_name")
    val entityTypeName: String,

    @Column("entity_app_name")
    val application: String
) : AbstractRefEntity<EntityTypeId>() {
    override fun getId() = entityTypeId
}
