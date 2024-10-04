package org.dbs.analyst.model.player

import org.dbs.player.enums.PlayerStatusEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("player_statuses_ref")
data class PlayerStatus(
    @Id
    @Column("player_status_id")
    val playerStatusId: PlayerStatusEnum,

    @Column("player_status_name")
    val playerStatusName: String,

    ) : AbstractRefEntity<Int>() {
    override fun getId() = playerStatusId.getCode()
}
