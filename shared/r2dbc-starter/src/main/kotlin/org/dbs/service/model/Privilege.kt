package org.dbs.service.model

import org.dbs.spring.ref.AbstractRefEntity
import org.dbs.entity.security.enums.PrivilegeGroupEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("core_privileges_ref")
data class Privilege(
    @Id
    @Column("privilege_id")
    val privilegeId: Int,

    @Column("privilege_group_id")
    val privilegeGroupId: PrivilegeGroupEnum,

    @Column("privilege_code")
    val privilegeCode: String,

    @Column("privilege_name")
    val privilegeName: String

) : AbstractRefEntity<Int>() {
    override fun getId() = privilegeId
}
