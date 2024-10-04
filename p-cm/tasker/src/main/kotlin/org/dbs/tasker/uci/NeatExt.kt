package org.dbs.tasker.uci

import net.andreinc.neatchess.client.UCI
import net.andreinc.neatchess.client.UCIResponse
import net.andreinc.neatchess.client.exception.UCIUnknownCommandException
import net.andreinc.neatchess.client.model.Analysis
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SysConst.UNCHECKED_CAST
import java.io.BufferedReader
import java.io.OutputStreamWriter

object NeatExt : Logging {

    fun UCI.analysis2(depth: Int, timeout: Long): UCIResponse<Analysis> {

        @Suppress(UNCHECKED_CAST)
        val writer = UCI::class.java.getDeclaredField("writer").let { it.trySetAccessible(); it.get(this) } as OutputStreamWriter
        @Suppress(UNCHECKED_CAST)
        val reader = UCI::class.java.getDeclaredField("reader").let {it.trySetAccessible(); it.get(this) } as BufferedReader


            //val output: MutableList<String?> = ArrayList<Any?>()
            writer.flush()
            writer.write("go depth $depth")
            writer.write("isready\n")
            writer.flush()
            var line = ""

            while (true) {
                if ((reader.readLine().also { line = it }) != null) {
                    if (line.contains("Unknown command")) {
                        throw UCIUnknownCommandException(line)
                    }

                    if (line.contains("Unexpected token")) {
                        throw UCIUnknownCommandException("Unexpected token: $line")
                    }

                    logger.debug { line }

                }
            }
        }
    }
