package org.dbs.rest.api

data class ServerStartTimeInfo(
    val serverStartTime: String,
    val serverLongStartTime: Long,
    val serverStringStartTime: String,
    val serverUpTime: String
)