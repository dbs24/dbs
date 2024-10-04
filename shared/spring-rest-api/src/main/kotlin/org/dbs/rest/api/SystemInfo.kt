package org.dbs.rest.api

import org.dbs.application.core.service.funcs.SysEnvFuncs.processBuildInfo
import org.dbs.application.core.service.funcs.SysEnvFuncs.processHandleInfo
import org.dbs.consts.DateTimeLong
import org.dbs.consts.OperDateString

data class SystemInfo(
    val sysInfo: String,
    val serverStartTime: OperDateString,
    val serverLongStartTime: DateTimeLong,
    val serverStringStartTime: OperDateString,
    val serverUpTime: OperDateString,
    val processInfo: String = processHandleInfo,
    val buildInfo: String = processBuildInfo
)