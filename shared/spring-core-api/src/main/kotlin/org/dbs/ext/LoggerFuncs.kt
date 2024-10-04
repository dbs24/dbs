package org.dbs.ext

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.dbs.application.core.service.funcs.SysEnvFuncs.findResourceFile
import org.dbs.consts.NoArg2Generic
import org.dbs.consts.SysEnvConst.Params.log4j2paramName
import java.io.File
import java.lang.System.getProperty
import java.lang.System.setProperty
import kotlin.system.measureTimeMillis

object LoggerFuncs {
    inline fun <T> KotlinLogger.measureExecTime(label: String, func: NoArg2Generic<T>) = run {
        val t: T
        measureTimeMillis {
            t = func()
        }.also {
            val logMsg = "$label: took $it ms"
            if (it > 10)
                debug(logMsg)
            else
                trace(logMsg)
        }
        t
    }

    inline fun <T> KotlinLogger.measureExecTime(label: String, maxExecTime: Int, func: NoArg2Generic<T>) = run {
        val t: T
        measureTimeMillis {
            t = func()
        }.also {
            if (it > 0) {
                debug { "$label: took $it ms" }
                logRequestInternal(it, maxExecTime) {
                    "$label: too slow test connection"
                }
            }
        }
        t
    }

    fun <T : Throwable, E : Throwable> KotlinLogger.logException(
        throwable: T,
        warnExceptionClass: Collection<Class<E>>
    ) =
        throwable.let {
            if (warnExceptionClass.any { warnClass -> warnClass.simpleName == it.javaClass.simpleName }) {
                this.warn { "${it.javaClass.canonicalName}: ${it.message}" }
            } else {
                this.error { it }
            }
        }

    fun <T : Throwable, E : Throwable> KotlinLogger.logException(throwable: T, warnExceptionClass: Class<E>) =
        logException(throwable, listOf(warnExceptionClass))

    inline fun KotlinLogger.logRequest(queryExecTime: Long, maxQueryTime: Int, logMessage: () -> String) =
        logMessage().apply {
            logRequestInternal(if (queryExecTime > 0) queryExecTime else 1, maxQueryTime) { this }
        }

    inline fun KotlinLogger.logRequestInternal(queryExecTime: Long, maxQueryTime: Int, warnMessage: () -> String) =
        warnMessage().let {
            val msg = "$it : took $queryExecTime ms"
            if (queryExecTime > maxQueryTime) {
                val slowMsg = "### too slow query - $msg"
                if (queryExecTime > 2 * maxQueryTime)
                    error { slowMsg } else
                    warn { slowMsg }
            } else {
                debug { msg }
            }
        }
    fun KotlinLogger.initLog4j2(application: String) {

        getProperty(log4j2paramName) ?: run {
            setProperty(log4j2paramName, findResourceFile("log4j2.xml", application))
        }

        // log4j
        getProperty(log4j2paramName)?.let {
            info { "$log4j2paramName is '$it'" }

            (LogManager.getContext(true) as LoggerContext).apply {
                stop()
                configLocation = File(it).toURI()
                info { "reconfig log4j (${this.configuration})" }
                initialize()
                updateLoggers()
                require(isInitialized) { "log4j is not initialized" }
                reconfigure()
                start()
                require(isStarted) { "log4j is not started" }
                info { "logger.isDebugEnabled = ${delegate.isDebugEnabled} " }
                info { "logger.isTraceEnabled = ${delegate.isTraceEnabled} " }

            }
        } ?: run {
            error { "parameter '$log4j2paramName' is null or empty".uppercase() }
        }
    }

    fun KotlinLogger.error(isError: Boolean, message: String) =
        if (isError) error(message) else debug(message)

}
