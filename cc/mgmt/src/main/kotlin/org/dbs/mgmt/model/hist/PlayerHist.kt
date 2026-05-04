package org.dbs.mgmt.model.hist

import com.fasterxml.jackson.annotation.JsonIgnore
import org.dbs.consts.AnyCode
import org.dbs.consts.BirthDate
import org.dbs.consts.CountryIsoCode
import org.dbs.consts.Email
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.dbs.consts.Password
import org.dbs.consts.UriPath
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.entity.core.v2.model.EntityCore
import org.dbs.player.PlayerCore.EntityTypes.ET_PLAYER
import org.dbs.player.PlayerId
import org.dbs.player.PlayerLogin
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

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

    @Column("status_id")
    val entityStatus: EntityStatusEnum,

    override val createDate: OperDate,

    override val modifyDate: OperDate,

    override val closeDate: OperDateNull = null,

) : EntityCore {

    @get:JsonIgnore
    override val entityId: EntityId get() = playerId

    @get:JsonIgnore
    override val type get() = ET_PLAYER

    @get:JsonIgnore
    override val status get() = entityStatus
}
