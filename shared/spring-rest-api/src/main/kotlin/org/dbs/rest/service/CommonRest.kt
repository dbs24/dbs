package org.dbs.rest.service

import org.dbs.application.core.nullsafe.StopWatcher
import org.dbs.application.core.service.funcs.GetNetworkAddress.allAddresses
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toString2
import org.dbs.application.core.service.funcs.SysEnvFuncs.memoryStatistics
import org.dbs.application.core.service.funcs.SysEnvFuncs.openFilesAmount
import org.dbs.consts.RestHttpConsts.ROUTE_URI_LIVENESS
import org.dbs.consts.RestHttpConsts.ROUTE_URI_READINESS
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_REMOTE_SHUTDOWN_ALLOWED
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_REMOTE_SHUTDOWN_ALLOWED
import org.dbs.consts.SysConst.FORMAT_dd_MM_yyyy__HH_mm_ss
import org.dbs.consts.SysConst.KOTLIN_VERSION
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.consts.SysConst.VOID_CLASS
import org.dbs.rest.api.IamReady
import org.dbs.rest.api.ReactiveRestProcessor
import org.dbs.rest.api.ServerStartTimeInfo
import org.dbs.rest.api.ShutdownRequest
import org.dbs.rest.api.SystemInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.LocalDateTime.now

@Service
@EnableScheduling
@EnableAsync
@ConditionalOnProperty(
    name = ["config.restful.common-routes.enabled"],
    havingValue = STRING_TRUE,
    matchIfMissing = true
)
class CommonRest : ReactiveRestProcessor() {
    @Value("\${$CONFIG_REMOTE_SHUTDOWN_ALLOWED:$VALUE_REMOTE_SHUTDOWN_ALLOWED}")
    private val remoteShutDownAllowed = false

    private val stopWatcher = StopWatcher.create("Server uptime")

    //==========================================================================
    fun liveness(request: ServerRequest) = this.processServerRequest(
        request, VOID_CLASS
    ) { buildLiveNessRecord() }

    //==========================================================================
    fun startTime(request: ServerRequest) = this.processServerRequest(
        request, VOID_CLASS
    ) { buildServerStartTimeRecord() }

    @Scheduled(fixedRateString = "\${security.manager.liveness.interval:1000000}")
    private fun buildDefaultLiveNessRecord() =
        "$ROUTE_URI_LIVENESS: Server start time: ${stopWatcher.stringStartDateTime}; i am fine, now is ${
            now().toString2()
        }, "
            .plus("$allAddresses, $memoryStatistics, $KOTLIN_VERSION, $openFilesAmount")
            .also { logger.info(it) }

    //==========================================================================
    fun readiness(request: ServerRequest) = processServerRequest(request, VOID_CLASS) {
        val responseString = "$ROUTE_URI_READINESS: I am ready"
        logger.info(responseString)
        IamReady(true)
    }

    //==========================================================================
    fun ready4ShutDown() = ShutdownRequest(true, "can Shutdown")

    //==========================================================================
    fun canShutdown(request: ServerRequest) =
        processServerRequest(
            request, VOID_CLASS
        ) {
            if (remoteShutDownAllowed) ready4ShutDown() else ShutdownRequest(
                false,
                "Remote Shutdown is not allowed"
            )
        }


    //==========================================================================
    private fun buildLiveNessRecord() = SystemInfo(
        buildDefaultLiveNessRecord(),
        stopWatcher.startDateTime.format(FORMAT_dd_MM_yyyy__HH_mm_ss),
        stopWatcher.startDateTime.toLong(),
        stopWatcher.startDateTime.toString2(),
        stopWatcher.getStringExecutionTime("Server uptime")
    )


    //==========================================================================
    fun buildServerStartTimeRecord() = ServerStartTimeInfo(
        stopWatcher.startDateTime.format(FORMAT_dd_MM_yyyy__HH_mm_ss),
        stopWatcher.startDateTime.toLong(),
        stopWatcher.startDateTime.toString2(),
        stopWatcher.getStringExecutionTime("Server uptime")
    )
}
