package org.dbs.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("core_issued_jwt")
data class IssuedJwt(
    @Id
    val jwtId: Long? = null,
    val issueDate: LocalDateTime,
    val validUntil: LocalDateTime,
    val jwt: String,
    val issuedTo: String,
    val tag: String? = null,
    val isRevoked: Boolean,
    val revokeDate: LocalDateTime? = null
)

@Table("core_refresh_jwt")
data class RefreshJwt(
    @Id
    val jwtId: Long? = null,
    val issueDate: LocalDateTime,
    val jwt: String,
    val parentJwtId: Long,
    val validUntil: LocalDateTime,
    val isRevoked: Boolean,
    val revokeDate: LocalDateTime? = null
)
