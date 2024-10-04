package org.dbs.spring.core.api

import org.dbs.application.core.service.funcs.TestFuncs.generateTestRangeInteger
import org.dbs.consts.SysConst.TIMEOUT_10000_MILLIS
import org.dbs.ext.LoggerFuncs.measureExecTime
import org.dbs.spring.core.api.ServiceLocator.findService
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers.boundedElastic
import reactor.core.scheduler.Schedulers.newParallel
import reactor.core.scheduler.Schedulers.newSingle
import java.net.InetSocketAddress
import java.net.Socket

abstract class AbstractApplicationService : AbstractApplicationBean() {

    val parallelScheduler: Scheduler by lazy { newParallel(javaClass.simpleName) }
    val boundedElastic: Scheduler by lazy { boundedElastic() }
    open val env by lazy { findService(ApplicationYmlService::class) }

    protected val singleScheduler: Scheduler by lazy { newSingle(javaClass.simpleName) }

    fun isHostPortAvailable(host: String, port: Int): Boolean = run {
        val actualPort = port.let { if (it > 0) it else 443 }
        val connectString = "$host:$actualPort"
        runCatching {
            Socket().use {
                logger.measureExecTime("$connectString: test connection") {
                  it.connect(InetSocketAddress(host, actualPort), TIMEOUT_10000_MILLIS)
                    runCatching {
                        it.apply {
                            if (isConnected) {
                                it.close()
                            }
                        }
                    }.getOrElse {
                        logger.error { "$connectString: can't close connection ($it)" }
                    }
                }
            }
            true
        }.getOrElse {
            logger.error { "${connectString}: $it" }
            false
        }
    }

    /**
     * Default code to match [org.dbs.application.core.service.funcs.Patterns.CODE_PATTERN] pattern
     */
    open fun generateEntityCode() = "${generateTestRangeInteger(100000000, 999999999)}"
}
