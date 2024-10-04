package org.dbs.mgmt.model.hist

import org.dbs.consts.OperDate
import org.dbs.consts.Password
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.dbs.consts.AnyCode
import org.dbs.consts.BirthDate
import org.dbs.consts.CountryIsoCode
import org.dbs.consts.UriPath
import org.dbs.player.PlayerCore.EntityTypes.ET_PLAYER
import org.dbs.player.PlayerId
import org.dbs.player.PlayerLogin
import org.dbs.service.v2.EntityCoreVal

@Table("cc_players_hist")
data class PlayerHist(
    @Id
    @Column("player_id")
    val playerId: PlayerId,
    @Column("actual_date")
    val actualDate: OperDate,
    @Column("player_login")
    val login: PlayerLogin,
    @Column("email")
    val email: String?,
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
)  : EntityCoreVal(playerId, ET_PLAYER)
