package org.dbs.tree.model.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.BirthDate
import org.dbs.consts.Email
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.consts.Password
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.user.UserCore.EntityTypes.ET_USER
import org.dbs.user.UserId
import org.dbs.user.UserLogin
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("ft_users")
data class User(
    @Id
    @Column("user_id")
    val userId: UserId? = null,
    @Column("user_login")
    val login: UserLogin,
    val email: Email?,
    val phone: String?,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val birthDate: BirthDate?,
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

    @get:JsonIgnore
    override val type get() = ET_USER

    @get:JsonIgnore
    override val status get() = entityStatus
}
