package org.dbs.analyst.model.hist

import org.dbs.spring.ref.AbstractRefEntity
import org.dbs.consts.BirthDay
import org.dbs.consts.OperDate
import org.dbs.consts.Password
import org.dbs.player.consts.ManagerAvatarImageNull
import org.dbs.player.consts.PlayerId
import org.dbs.entity.core.AbstractEntity
import org.dbs.player.PlayerLogin
import org.dbs.player.enums.PlayerStatusEnum
import org.dbs.ref.serv.enums.CountryEnum
import org.dbs.ref.serv.enums.GenderEnum
import org.dbs.ref.serv.enums.RegionEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("player_hist")
data class PlayerHist(
    @Id
    @Column("player_id")
    val playerId: PlayerId,
    @Column("actual_date")
    val actualDate: OperDate,
    @Column("player_login")
    val login: PlayerLogin,
    @Column("player_status_id")
    val status: PlayerStatusEnum,
    @Column("email")
    val email: String,
    @Column("phone")
    val phone: String?,
    @Column("first_name")
    val firstName: String,
    @Column("last_name")
    val lastName: String,
    @Column("token")
    val token: String,
    @Column("password_hash")
    val password: Password,
)  : AbstractRefEntity<PlayerId>() {
    override fun getId(): PlayerId = playerId
}
