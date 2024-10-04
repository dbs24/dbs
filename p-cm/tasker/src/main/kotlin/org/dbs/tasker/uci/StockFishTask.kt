package org.dbs.tasker.uci

import net.andreinc.neatchess.client.UCI
import org.dbs.player.kafka.UciTask
import java.io.Closeable
import kotlin.system.measureTimeMillis

//@Service
//@Lazy(false)
class StockFishTask(task: UciTask) : UciEngine {

    override val task = task
    override val uci by lazy { UCI(3600000L) }
    override val engineName = "stockfish-ubuntu-x86-64-modern"

    init {
        setupEngine()
    }
}
