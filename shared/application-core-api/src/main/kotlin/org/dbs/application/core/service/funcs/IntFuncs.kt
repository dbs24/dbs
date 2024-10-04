package org.dbs.application.core.service.funcs

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.LOCALDATE_NULL
import java.time.LocalDate

object IntFuncs {
    fun Int?.toLocalDate(): LocalDate? =
        this?.toLocalDate() ?: LOCALDATE_NULL

    fun Int.toLocalDate(): LocalDate =
        this.run { LocalDate.ofEpochDay(this.toLong()) }

    fun Int.to32bitString(): String =
        Integer.toBinaryString(this).padStart(Int.SIZE_BITS, '0')

    fun Int.thereX() = "There ${if (this <= 1) "is" else "are"} ${if (this == 0) "no" else "$this"} "

    fun Int.thereX(items: String) = "${this.thereX()}$items"

    fun Int.errorS() = "${thereX()}error${endS()}"

    fun Int.endS(ends: String = "s") = "${if (this == 1) EMPTY_STRING else ends} "

    fun Int.elementsInList() = "${this.thereX()} element${this.endS()} in list"

    fun Int.pagesCount(pageSize: Int): Int = ((this / pageSize) + if (this % pageSize > 0) 1 else 0)
}
