package org.dbs.tasker.uci

import net.andreinc.neatchess.client.UCI
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.SysEnvFuncs.findResourceFile
import org.dbs.player.kafka.UciTask
import org.dbs.tasker.uci.NeatExt.analysis2
import java.io.Closeable
import kotlin.system.measureTimeMillis

interface UciEngine : Logging, Closeable {
    val uci: UCI
    val engineName: String
    val task: UciTask

    fun setupEngine() {
        findResourceFile(engineName) {
            uci.start(it)
            logger.debug { "starting chess engine: $it" }
            logger.debug { "engineInfo: ${uci.engineInfo}" }
        }
    }

    fun runTask() {
        measureTimeMillis {

            logger.debug { "start processing task: [$task]" }

            kotlin.runCatching {

                with(task) {

                    logger.debug { "MultiPV: $moves, timeout: $timeout, depth: $depth" }
                    uci.setOption("MultiPV", moves.toString(), timeout)
                    uci.uciNewGame()
                    val response = uci.positionFen(fen, timeout)
                    logger.debug { "response fen: $response"  }


                    //val bestMove = uci.bestMove(depth, timeout).resultOrThrow

                    val analysis = uci.analysis2(depth, timeout)
                    logger.debug { "response analysis: $response"  }

                    val anal = analysis.resultOrThrow

                    logger.debug { "anal = $anal"  }

                    val moves = anal.allMoves
                    val bestMove = anal.bestMove

                    logger.debug { "Best move ($depth, $timeout): 'lan: ${bestMove.lan}, pv: ${bestMove.pv}' ($fen)" }
                    logger.debug { "all moves: ${moves.map { it }}" }
                }

            }.getOrElse {
                logger.warn { it }
            }

        }.also {
            logger.debug { "executed in: $it ms" }
            close()
        }
    }

//    fun sendSolution(fen: Fen, bestMove: String, bestMoves: Collection<Move>) {
//        kafkaUniversalService.send(CM_SEND_SOLUTION, UciSolution(fen, bestMove, bestMoves))
//    }

    override fun close() {
        logger.debug { "stopping $engineName ($task)" }
        uci.close()
    }

}
