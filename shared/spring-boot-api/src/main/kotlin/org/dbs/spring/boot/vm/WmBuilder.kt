package org.dbs.spring.boot.vm

import com.sun.management.HotSpotDiagnosticMXBean
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.LongFuncs.toNumber
import org.dbs.application.core.service.funcs.ServiceFuncs.createOrderedMap
import org.dbs.consts.SysConst.BLANK_SYMBOL
import java.lang.management.ManagementFactory

@JvmInline
value class WmBuilder(private val set: Set<String>) : Logging {

    fun buildLog(): String = set.let {
        val map = createOrderedMap<String, String>()
        var maxLength = 0

        val bb = StringBuilder("\n")
        ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean::class.java).apply {
            it.forEach {
                map[it] = getVMOption(it).value.toLong().toNumber()
                if (it.length > maxLength) maxLength = it.length
            }
            map.forEach {
                bb.append(it.key + BLANK_SYMBOL.repeat(maxLength - it.key.length + 1) + ": " + it.value + "\n")
            }
        }
        bb.toString()
    }
}
