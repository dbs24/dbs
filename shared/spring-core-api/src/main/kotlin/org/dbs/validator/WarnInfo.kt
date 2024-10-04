package org.dbs.validator

class WarnInfo(private val warnType: WarnType, private val warnMsg: String) {

    companion object {
        fun create(warnType: WarnType, warnMsg: String): WarnInfo? {
            return WarnInfo.create(warnType, warnMsg)
        }
    }
}
