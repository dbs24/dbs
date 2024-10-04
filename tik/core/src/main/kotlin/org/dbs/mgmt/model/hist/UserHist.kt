package org.dbs.mgmt.model.hist

import org.dbs.consts.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.dbs.service.v2.EntityCoreVal
import org.dbs.customer.UserCore.EntityTypes.ET_USER

@Table("tt_players_hist")
data class UserHist(
    @Id
    @Column("player_id")
    val playerId: EntityId,
    @Column("actual_date")
    val actualDate: OperDate,
    @Column("player_login")
    val login: Login,
    @Column("email")
    val email: Email?,
    @Column("phone")
    val phone: String?,
    @Column("first_name")
    val firstName: String?,
    @Column("middle_name")
    val middleName: String?,
    @Column("last_name")
    val lastName: String?,
    @Column("birth_date")
    val birthDate: BirthDate?,
    @Column("password_hash")
    val password: Password?,
    val country: CountryIsoCode?,
    val gender: AnyCode?,
    @Column("avatar_path")
    val avatar: UriPath?,
    @Column("small_avatar_path")
    val smallAvatar: UriPath?,
)  : EntityCoreVal(playerId, ET_USER)
