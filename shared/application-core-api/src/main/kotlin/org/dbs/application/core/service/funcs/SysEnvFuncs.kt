package org.dbs.application.core.service.funcs

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toNum
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.application.core.service.funcs.LongFuncs.toNumber
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysEnvConst.SysProperties.USER_DIR
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.System.getProperty
import java.time.Instant
import java.util.stream.Stream


typealias FoundFileAction = (String) -> Unit

object SysEnvFuncs {
    //==========================================================================

    val appCreateTime by lazy { LateInitVal<Instant>() }
    private val maxOpenFilesCalc by lazy { MaxOpenFilesCalc() }
    const val defaultDepth = 10

    val cPLoaders: String
        get() {
            val cl = ClassLoader.getSystemClassLoader()
            return Stream.of(*cl.definedPackages)
                .map { url: Package -> url.name }
                .sorted()
                .reduce(
                    "URLClassLoader \n "
                ) { x: String, y: String? -> x + " " + String.format("%s\n", y) }
        }

    //==========================================================================
    @JvmStatic
    val memoryStatistics
        get() = "Heap utilization statistics [PID=${ProcessHandle.current().pid()}]".run {

            val instance = Runtime.getRuntime()

            this.plus(", Total Memory: ${instance.totalMemory().toNumber()} bytes")
                .plus(", Free Memory: ${instance.freeMemory().toNumber()} bytes")
                .plus(", Used Memory: ${(instance.totalMemory() - instance.freeMemory()).toNumber()} bytes")
                .plus(", Max Memory: ${(instance.totalMemory() - instance.freeMemory()).toNumber()} bytes")
                .plus(", Processors/Threads: ${instance.availableProcessors()}")
        }

    @JvmStatic
    val openFilesAmount get() = maxOpenFilesCalc.calculateMaxOpenedFiles()

    @JvmStatic
    val processHandleInfo
        get() = "ProcessHandle.info [${ProcessHandle.current().info()}]]"

    @JvmStatic
    val processBuildInfo
        get() = appCreateTime.value.toEpochMilli().toLocalDateTime().toNum()

    @JvmStatic
    fun runTimeExec(cmd: String) = let {
        var s: String?
        val sb = StringBuilder(1024)
        val p: Process
        try {
            p = Runtime.getRuntime().exec(cmd)
            val br = BufferedReader(InputStreamReader(p.inputStream))
            while (br.readLine().also { s = it }.hashCode() != 0) sb.append(String.format("%s: %s\n", cmd, s))
            p.waitFor()
            sb.append("$cmd: exit: ${p.exitValue()}")
            p.destroy()
        } catch (e: Throwable) {
            // silent exception
            e.printStackTrace()
            println(e)
        }
        sb.toString()
    }

    @JvmStatic
    fun runTimeExec(cmds: Array<String>) = let {
        val sb = java.lang.StringBuilder(1024)
        val rt = Runtime.getRuntime()
        try {
            val proc = rt.exec(cmds)
            val `is` = BufferedReader(InputStreamReader(proc.inputStream))
            var line: String?
            while (`is`.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            sb.append(e.message)
        }
        sb.toString()
    }

    @JvmStatic
    fun runTimeExecSh(cmd: String): String {
        val multiCmd = arrayOf("/bin/sh", "-c", cmd)
        return runTimeExec(multiCmd)
    }

    @JvmStatic
    fun findResourceFile(fileName: String, containFolder: String, maxDepth: Int, foundAction: FoundFileAction = {}) =

        /**
         * Find file and return full path.
         *
         * @apiNote
         *
         * @param      fileName   short file name
         * @param      containFolder   contain folder
         * @param      maxDepth   max tree depth
         * @param      foundAction success action lamda
         * @return     absolute path
         *
         * @throws     IllegalStateException  if file not found.
         */

        File(getProperty(USER_DIR))
            .walk()
            .maxDepth(maxDepth)
            .filter {
                !it.isDirectory
                        && !it.absolutePath.contains(".idea")
                        && !it.absolutePath.contains("build")
                        && (containFolder.isEmpty() || it.absolutePath.contains(containFolder))
                        && it.name == fileName
            }
            .firstOrNull()
            ?.run { foundAction(canonicalPath); canonicalPath
                .also { System.out.println("found resource file [$canonicalPath], containFolder=$containFolder") } }
            ?: run {
                val errMsg = "file not found ($fileName), containFolder=$containFolder"
                //error(errMsg)
                errMsg.also { System.out.println("#### $errMsg") }
            }

    @JvmStatic
    fun findResourceFile(fileName: String, containFolder: String) =

        /**
         * Find file and return full path.
         *
         * @apiNote
         *
         * @param      fileName   short file name
         * @param      containFolder   contain folder
         * @return     absolute path
         *
         * @throws     IllegalStateException  if file not found.
         */

        findResourceFile(fileName, containFolder, defaultDepth) {}

    @JvmStatic
    fun findResourceFile(fileName: String) =

        /**
         * Find file and return full path.
         *
         * @apiNote
         *
         * @param      fileName   short file name
         * @return     absolute path
         *
         * @throws     IllegalStateException  if file not found.
         */

        findResourceFile(fileName, EMPTY_STRING, defaultDepth) {}

    @JvmStatic
    fun findResourceFile(fileName: String, foundAction: FoundFileAction) =

        /**
         * Find file and return full path.
         *
         * @apiNote
         *
         * @param      fileName   short file name
         * @param      foundAction success action lamda
         * @return     absolute path
         *
         * @throws     IllegalStateException  if file not found.
         */

        findResourceFile(fileName, EMPTY_STRING, defaultDepth, foundAction)

    @JvmStatic
    fun findResourceFile(fileName: String, containFolder: String, foundAction: FoundFileAction) =

        /**
         * Find file and return full path.
         *
         * @apiNote
         *
         * @param      fileName   short file name
         * @param      containFolder   contain folder*
         * @param      foundAction success action lamda
         * @return     absolute path
         *
         * @throws     IllegalStateException  if file not found.
         */

        findResourceFile(fileName, containFolder, defaultDepth, foundAction)

}
