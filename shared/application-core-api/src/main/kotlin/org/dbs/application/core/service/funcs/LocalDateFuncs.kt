package org.dbs.application.core.service.funcs

import org.dbs.consts.SysConst.INT_NULL
import java.time.LocalDate

object LocalDateFuncs {
    fun LocalDate?.toInt(): Int? =
        this?.run { this.toInt() } ?: INT_NULL

    fun LocalDate.toInt(): Int = this.toEpochDay().toInt()

    fun LocalDate?.isNull() = (this.hashCode() == 0)

    fun LocalDate?.isNotNull() = (this.hashCode() != 0)

    fun LocalDate.toNum(): String = year.toString().padStart(2, '0') + monthValue.toString().padStart(2, '0') + dayOfMonth.toString()

}
