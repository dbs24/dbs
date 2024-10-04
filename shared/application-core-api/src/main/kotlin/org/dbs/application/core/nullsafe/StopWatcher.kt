package org.dbs.application.core.nullsafe

import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.d1d2Diff
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.getStringDateTime
import org.dbs.consts.SysConst.DateTimeConsts.msBorder
import org.dbs.consts.SysConst.IntConsts.FIVE
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId.of
import java.time.temporal.ChronoUnit

class StopWatcher() {
    private var unitName: String
        private set
    val startDateTime: LocalDateTime = now(of(defaultZoneId))
    val stringStartDateTime: String
        get() = startDateTime.getStringDateTime()

    constructor(unitName: String) : this() {
        this.unitName = unitName
    }

    private fun getRunnigProcName(index: Int) = Thread.currentThread().stackTrace[index].run {
        "${this.className}.${this.methodName} [${this.fileName}]"
    }

    val executionTime: Long
        get() = ChronoUnit.MILLIS.between(startDateTime, now(of(defaultZoneId)))

    //==========================================================================

    init {
        unitName = getRunnigProcName(FIVE)
    }

    val stringExecutionTime: String
        get() = getStringExecutionTime("executed in")

    //==========================================================================
    fun getStringExecutionTime(info: String) =
        if (executionTime < msBorder) {
            "$unitName: $info $executionTime ms"
        } else {
            "$info: ${d1d2Diff(executionTime)}"
        }

    companion object {
        const val defaultZoneId = "Europe/Moscow"
        fun create() = StopWatcher()

        fun create(unitName: String) = StopWatcher(unitName)

    }
}
