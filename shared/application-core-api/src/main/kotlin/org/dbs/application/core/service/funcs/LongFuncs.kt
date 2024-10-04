package org.dbs.application.core.service.funcs

import org.dbs.application.core.service.funcs.IntFuncs.pagesCount
import org.dbs.application.core.service.funcs.IntFuncs.thereX
import org.dbs.application.core.service.funcs.StringFuncs.firstAndLastN
import org.dbs.application.core.service.funcs.StringFuncs.lastN
import org.dbs.application.core.service.funcs.StringFuncs.locale
import org.dbs.consts.SysConst.LOCALDATETIME_NULL
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object LongFuncs {

    fun Long?.toLocalDateTime(): LocalDateTime? =
        this?.run { this.toLocalDateTime() } ?: LOCALDATETIME_NULL

    fun Long.toLocalDateTime(): LocalDateTime =
        Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

    fun Long.secondsToLocalDateTime(): LocalDateTime =
        Instant.ofEpochMilli(this.times(1000)).atZone(ZoneId.systemDefault()).toLocalDateTime()

    fun Long.toNumber(): String = NumberFormat.getInstance(locale).format(this)

    fun Long?.isNull() = (this.hashCode() == 0)

    fun Long?.isNotNull() = (this.hashCode() != 0)

    fun Long.pagesCount(pageSize: Int): Int = this.toInt().pagesCount(pageSize)

    fun Long.thereX(items: String) = "${this.toInt().thereX()} $items"

    fun Long.cardMask(): String = this.toString().firstAndLastN({4}, "*").substring(0, 14)

    fun Long.cardLast4(): String = this.toString().lastN({ 4 })


}
