package org.dbs.application.core.service.funcs


import org.dbs.application.core.service.funcs.SysEnvFuncs.runTimeExecSh
import org.dbs.consts.SysConst.INTEGER_ZERO
import org.dbs.consts.SysConst.NOT_DEFINED
import org.dbs.consts.SysEnvConst.MAX_OPEN_FILES_KEY_WORD
import java.lang.ProcessHandle.current
import java.util.*
import java.util.regex.Pattern

class MaxOpenFilesCalc {

    private val currentPid by lazy { current().pid() }
    private val openedFilesCmd by lazy { "ls -l -d /proc/$currentPid/fd/* | wc -l" }
    private val cmdGetMaxOpened by lazy { "cat /proc/$currentPid/limits | grep \"$MAX_OPEN_FILES_KEY_WORD\"" }
    private val mofPcFree = 10

    private var lastOpenedFilesAmt = INTEGER_ZERO
    fun calculateMaxOpenedFiles(): String = run {
        val openedFilesStr: String = runTimeExecSh(openedFilesCmd)
        val openedFilesLimitCmdStr: String = runTimeExecSh(cmdGetMaxOpened)
        val openFilesAmount: String

        val regex = "(\\d+)"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(openedFilesLimitCmdStr)
        val maxLimit = if (matcher.find()) matcher.group(1) else NOT_DEFINED

        if (maxLimit == NOT_DEFINED) {
            openFilesAmount = "$MAX_OPEN_FILES_KEY_WORD: unknown or not defined"
        } else {
            val maxOpenedFiles = maxLimit.toInt()
            val openedFiles = openedFilesStr.toInt()
            if (lastOpenedFilesAmt != openedFiles) {
                lastOpenedFilesAmt = openedFiles
                openFilesAmount = "$MAX_OPEN_FILES_KEY_WORD: $openedFilesStr/$maxLimit"
            } else
                if (openedFiles.toFloat() / maxOpenedFiles.toFloat() + mofPcFree.toFloat() / 100 >= 1) {
                    openFilesAmount =
                        "PID: $currentPid, max-opened-files violation (used $openedFiles/$maxOpenedFiles) "
                            .uppercase(Locale.getDefault())

                } else {
                    openFilesAmount = "$MAX_OPEN_FILES_KEY_WORD: $openedFilesStr/$maxLimit"
                }
        }
        openFilesAmount
    }
}
