package org.dbs.application.core.nullsafe

import org.dbs.consts.SysConst.IntConsts.FIFTY
import java.util.*

/**
 *
 * @author kazyra_d
 */
class StackTraceInfo(private val throwable: Throwable?) {
    var printRecord = " StackTrace details: \n"
        private set
    private val shift = "############"

    init {
        addTraceRecord(
            "$shift ${throwable!!.javaClass.canonicalName}: '${throwable.message}'\n"
        )
    }

    fun addTraceRecord(recInfo: String) {
        printRecord = printRecord.plus(recInfo)
    }

    private fun addTraceElement(ste: StackTraceElement) {
        printRecord = printRecord.plus("$shift ${ste.className}.${ste.methodName}() (${ste.lineNumber})\n")
    }

    //                .sorted((st1, st2) -> {
    //                    return (Integer.valueOf(st1.getLineNumber()).compareTo(st2.getLineNumber()));
    //                })
    val stringStackTraceInfo: String
        get() {
            Arrays.stream(throwable!!.stackTrace) //                .sorted((st1, st2) -> {
                .limit(FIFTY.toLong())
                .forEach { ste: StackTraceElement -> addTraceElement(ste) }
            return printRecord
        }
}
