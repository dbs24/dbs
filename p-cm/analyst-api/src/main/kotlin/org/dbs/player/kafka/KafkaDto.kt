package org.dbs.player.kafka

import org.dbs.kafka.api.KafkaDocument
import org.dbs.player.consts.Fen


data class UciTask(
    val fen: Fen,
    val moves: Int,
    val depth: Int,
    val timeout: Long
) : KafkaDocument


data class Move(
    val move: String,
    val depth: Int,
    val pv: Int,
)

data class UciSolution(
    val fen: Fen,
    val bestMove: String,
    val bestMoves: Collection<Move>,
) : KafkaDocument
