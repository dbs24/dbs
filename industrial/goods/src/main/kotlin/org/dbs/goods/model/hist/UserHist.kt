package org.dbs.goods.model.hist

import org.dbs.consts.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.dbs.goods.UserCore.EntityTypes.ET_USER
import org.dbs.goods.UserId
import org.dbs.goods.UserLogin
import org.dbs.service.v2.EntityCoreVal

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
)  : EntityCoreVal(userId, ET_USER)
