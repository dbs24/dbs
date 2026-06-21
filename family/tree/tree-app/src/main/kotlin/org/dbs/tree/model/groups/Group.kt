package org.dbs.tree.model.groups

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.user.FamilyTreeCore.EntityTypes.ET_GROUP
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ft_service_groups")
data class ServiceGroup(
    @Id
    @Column("group_id")
    val groupId: Long? = null,
    val shortName: String,
    val fullName: String?,
    @Column("status_id")
    val entityStatus: EntityStatusEnum,
    override val createDate: OperDate,
    override val modifyDate: OperDate,
    override val closeDate: OperDateNull = null,

    ) : EntityCore {

    @get:JsonIgnore
    override val entityId: EntityId? get() = groupId

    @get:JsonIgnore
    override val type get() = ET_GROUP

    @get:JsonIgnore
    override val status get() = entityStatus
}
