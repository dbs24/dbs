package org.dbs.application.core.service.funcs

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.FORMAT_dd_MM_yyyy__HH_mm_ss
import org.dbs.consts.SysConst.IntConsts.STR64
import org.dbs.consts.SysConst.LONG_NULL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

object LocalDateTimeFuncs {
    fun LocalDateTime.toLong() =
        ZonedDateTime.of(this, ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun Instant.toString2(): String = this.atZone(ZoneId.systemDefault()).toLocalDateTime().toString2()
    fun LocalDateTime?.toLong() =
        this?.run { this.toLong() } ?: LONG_NULL

    fun LocalDateTime.toString2(): String = this.format(FORMAT_dd_MM_yyyy__HH_mm_ss)

    fun LocalDateTime.toNum(): String = year.toString().padStart(2, '0') + monthValue.toString().padStart(2, '0') + dayOfMonth.toString()
        .padStart(2, '0') + "_" +
            hour.toString().padStart(2, '0') + minute.toString().padStart(2, '0') +
            " [${this.toString2()}]"

    fun LocalDateTime?.isNull() = (this.hashCode() == 0)

    fun LocalDateTime?.isNotNull() = (this.hashCode() != 0)

    fun LocalDateTime.getStringDateTime() =
        if (this.isNull()) EMPTY_STRING else this.format(FORMAT_dd_MM_yyyy__HH_mm_ss)

    fun d1d2Diff(lmillis: Long) = StringBuilder(STR64).run {
        var millis = lmillis
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        millis = millis - TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis = millis - TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis = millis - TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

        var needAppend = days > 0
        if (needAppend) {
            append(days)
            append(" day(s) ")
        }
        needAppend = needAppend || hours > 0
        if (needAppend) {
            append(hours)
            append(" hour(s) ")
        }
        needAppend = needAppend || minutes > 0
        if (needAppend) {
            append(minutes)
            append(" minute(s) ")
        }
        needAppend = needAppend || seconds > 0
        if (needAppend) {
            append(seconds)
            append(" second(s) ")
        }
        toString()
    }

    fun LocalDateTime.d1d2Diff(d2: LocalDateTime) = d1d2Diff(ChronoUnit.MILLIS.between(this, d2))

}
