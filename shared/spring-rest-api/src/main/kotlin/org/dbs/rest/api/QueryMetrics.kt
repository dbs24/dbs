package org.dbs.rest.api


import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.nullsafe.StopWatcher
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SysConst.STRING_NULL
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

class QueryMetrics : Logging {
    val qryCounter = AtomicInteger()

    class QueryMetric(
        val seq: Int,
        val stopWatcher: StopWatcher,
        val execMsg: String?
    ) : Logging

    val queries = createCollection<QueryMetric>()

    init {
        qryCounter.set(0)
    }

    fun registryQuery(methodName: String, path: String) =
        qryCounter.incrementAndGet().also {
            queries.add(QueryMetric(it, StopWatcher.create(methodName + path), STRING_NULL))
        }

    fun finishQuery(num: Int, signal: String) =
        queries.stream().filter { it.seq == num }
            .findFirst()
            .ifPresentOrElse({
                logger.debug("$signal: ${it.stopWatcher.stringExecutionTime}")
                queries.remove(it)
            }) { logger.warn("Unknown query or removed: $num") }


    fun filterExecuted() {
        queries.forEach(Consumer { qry: QueryMetric ->
            if (qry.stopWatcher.executionTime > 5000) {
                logger.warn(
                    ("bad on broken query: " +
                            "${qry.stopWatcher.stringExecutionTime} ").uppercase(Locale.getDefault())
                )
                queries.remove(qry)
            }
        })
    }
}
