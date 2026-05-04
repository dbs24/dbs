package org.dbs.goods.model.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.Email
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.consts.Password
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.goods.UserCore.EntityTypes.ET_USER
import org.dbs.goods.UserId
import org.dbs.goods.UserLogin
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    val userId: UserId? = null,
    @Column("user_login")
    val login: UserLogin,
    @Column("email")
    val email: Email?,
    val firstName: String?,
    val lastName: String?,
    @Column("password_hash")
    val password: Password?,
    @Column("status_id")
    val entityStatus: EntityStatusEnum,
    override val createDate: OperDate,
    override val modifyDate: OperDate,
    override val closeDate: OperDateNull = null,
) : EntityCore {

    @get:JsonIgnore
    override val entityId: EntityId? get() = userId

    override fun entityType() = ET_USER

    override fun status() = entityStatus
}

