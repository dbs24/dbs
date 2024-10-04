package org.dbs.analyst.model.solution

import org.dbs.consts.Password
import org.dbs.player.consts.PlayerId
import org.dbs.player.PlayerLogin
import org.dbs.player.enums.PlayerStatusEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.dbs.player.consts.Fen
import org.dbs.player.consts.SolutionId
import org.dbs.player.enums.SolutionStatusEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("solution")
data class Solution(
    @Id
    @Column("solution_id")
    val solutionId: SolutionId,
    @Column("player_id")
    val playerId: PlayerId,
    @Column("fen")
    val fen: Fen,
    @Column("solution_status_id")
    val status: SolutionStatusEnum,
    @Column("depth")
    val depth: Int,
    @Column("timeout")
    val timeout: Int,
    @Column("white_move")
    val whiteMove: Boolean,
    @Column("move")
    val move: String,
)  : AbstractRefEntity<PlayerId>() {
    override fun getId(): PlayerId = playerId
}
