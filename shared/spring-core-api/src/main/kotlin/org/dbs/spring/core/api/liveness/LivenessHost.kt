package org.dbs.spring.core.api.liveness

import org.dbs.consts.OperDateNull
import org.dbs.consts.StringNoteNull
import org.dbs.consts.SysConst.EMPTY_STRING

data class LivenessHost(
    val trackingHost: TrackingHost,
    var failAttempts: Int = 0,
    var connectionInfo: StringNoteNull = null,
    var lastOnline: OperDateNull = null,
    var lastTestAttempt: OperDateNull = null,
    var note: StringNoteNull = EMPTY_STRING,
    var needLogging: Boolean = true
)

data class TrackingHost(
    val host: String,
    val port: Int,
    val serviceName: String,
)
