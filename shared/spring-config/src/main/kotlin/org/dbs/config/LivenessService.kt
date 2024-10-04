package org.dbs.config

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_LIVENESS
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.ServiceConsts.TEST_CONNECTION_DEFAULT_TIMEOUT
import org.dbs.consts.SpringCoreConst.ServiceConsts.TEST_CONNECTION_TIMEOUT_STEP
import org.dbs.consts.SysConst.INTEGER_ZERO
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ApplicationBean.Companion.externalAddresses
import org.dbs.spring.core.api.liveness.LivenessHost
import org.dbs.spring.core.api.liveness.TrackingHost
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.InetSocketAddress
import java.net.Socket
import java.time.LocalDateTime.now
import java.util.concurrent.TimeUnit.SECONDS

@Service
@ConfigurationProperties(CONFIG_LIVENESS)
@ConditionalOnProperty(name = [JUNIT_MODE], havingValue = "false", matchIfMissing = true)
class LivenessService : AbstractApplicationService() {

    val hosts: Collection<TrackingHost> by lazy { createCollection() }
    override fun initialize() = super.initialize().also {
        externalAddresses.apply {
            hosts.also {
                logger.debug { "extended hosts list for tracking (${hosts.size}): $hosts" }
            }
                .forEach { add(LivenessHost(TrackingHost(it.host, it.port, it.serviceName))) }
        }
    }

    @Scheduled(
        initialDelay = 1,
        fixedRateString = "\${config.liveness.internal-services.range:60}",
        timeUnit = SECONDS
    )
    fun checkDueTimeTimeout() = runBlocking {
        externalAddresses
            .asSequence()
            .distinctBy { "${it.trackingHost.host}:${it.trackingHost.port}" }
            .forEach {
                //======================================================================================================
                suspend fun testBySocket(
                    host: String,
                    port: Int,
                    serviceName: String,
                    timeout: Long,
                    needLogging: Boolean,
                    failAttempts: Int,
                ) =
                    let {
                        val connectionInfo = LateInitVal<ConnectionInfo>()
                        val limitAttempts = 5
                        var currAttempt = 1
                        var currTimeout = timeout
                        var successConnection = false

                        do {
                            try {
                                Socket().use { socket ->

                                    socket.connect(InetSocketAddress(host, port), timeout.toInt())
                                    connectionInfo.init(
                                        ConnectionInfo(socket.toString()).also { it.failAttempts = failAttempts })
                                    successConnection = true

                                    if (needLogging)
                                        logger.debug(
                                            "* $host:$port ($serviceName) [${connectionInfo.value.connectionInfo}, " +
                                                    "${connectionInfo.value.note}, " +
                                                    "failAttempts = ${connectionInfo.value.failAttempts}]"
                                        )

                                    try {
                                        if (socket.isConnected) socket.close()
                                    } catch (e: Throwable) {
                                        logger.error { "can't close connection $host:$port: ($serviceName) $e " }
                                    }
                                }
                            } catch (e: Throwable) {
                                if (currAttempt == limitAttempts) {
                                    connectionInfo.init(ConnectionInfo("### can't connect 2 $host:$port ($serviceName), ($currTimeout ms)"))
                                        .also {
                                            it.note = e.toString()
                                            it.failAttempts = failAttempts + 1
                                        }
                                    logger.error {
                                        "${connectionInfo.value.connectionInfo}, " +
                                                "${connectionInfo.value.note}, " +
                                                "failAttempts = ${connectionInfo.value.failAttempts}"
                                    }
                                } else {
                                    delay(currTimeout)
                                }
                                currAttempt++
                                currTimeout += TEST_CONNECTION_TIMEOUT_STEP
                            }
                            //logger.debug { "$host:$port: successConnection=$successConnection;currAttempt=$currAttempt" }
                        } while ((!successConnection) && (currAttempt <= limitAttempts))

                        if ((successConnection) && (currAttempt > 1))
                            logger.warn {
                                "too slow connection to $host:$port ($serviceName) ($currTimeout ms, " +
                                        "fails attempts: ${connectionInfo.value.failAttempts})"
                            }
                        connectionInfo
                    }
                //======================================================================================================
                launch {
                    it.lastTestAttempt = now()
                    testBySocket(
                        it.trackingHost.host,
                        it.trackingHost.port,
                        it.trackingHost.serviceName,
                        TEST_CONNECTION_DEFAULT_TIMEOUT,
                        it.needLogging,
                        it.failAttempts
                    )
                        .apply {
                            it.needLogging = true
                            it.connectionInfo = this.value.connectionInfo
                            it.note = this.value.note
                            it.failAttempts = this.value.failAttempts
                            it.note ?: run {
                                // success
                                it.lastOnline = it.lastTestAttempt
                                it.needLogging = false
                            }
                        }
                }
            }
    }

    data class ConnectionInfo(
        val connectionInfo: String,
        var note: String? = STRING_NULL,
        var failAttempts: Int = INTEGER_ZERO
    )
}
