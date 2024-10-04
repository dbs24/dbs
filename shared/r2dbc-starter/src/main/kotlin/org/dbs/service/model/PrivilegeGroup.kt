package org.dbs.service.model

import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_privileges_groups_ref")
data class PrivilegeGroup(
    @Id
    @Column("privilege_group_id")
    val privilegeGroupId: Int,

    @Column("privilege_group_code")
    val privilegeGroupCode: String,

    @Column("privilege_group_name")
    val privilegeGroupName: String,

    ) : AbstractRefEntity<Int>() {
    override fun getId() = privilegeGroupId
}
