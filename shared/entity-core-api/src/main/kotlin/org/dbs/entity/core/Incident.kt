package org.dbs.entity.core

import org.dbs.consts.OperDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("core_incidents")
data class Incident(
    @Id
    val incidentId: String,
    val source: String,
    val path: String,
    val createDate: OperDate = LocalDateTime.now(),
    val stackTrace: String,
    val osOpenFiles: Long,
    val jvmFreeMemoryBytes: Long,
    val jvmTotalMemoryBytes: Long,
    val jvmMaxMemoryBytes: Long
)
