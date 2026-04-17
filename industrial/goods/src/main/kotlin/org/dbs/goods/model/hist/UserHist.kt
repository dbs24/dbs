package org.dbs.goods.model.hist

import org.dbs.consts.Email
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.consts.Password
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.EntityTypeEnum
import org.dbs.goods.UserCore.EntityTypes.ET_USER
import org.dbs.goods.UserId
import org.dbs.goods.UserLogin
import org.dbs.service.v2.EntityCoreVal
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("cc_users_hist")
data class UserHist(
    @Id
    @Column("user_id")
    val userId: UserId,
    @Column("actual_date")
    val actualDate: OperDate,
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
    val createDate: OperDate,
    val modifyDate: OperDate,
    val closeDate: OperDateNull = null,
) : EntityCoreVal(userId, ET_USER) {
    @Transient override val entityType: EntityTypeEnum = ET_USER
    override fun status() = entityStatus
}
