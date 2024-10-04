package org.dbs.auth.server.model

import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("tkn_issued_jwt")
data class IssuedJwt(
    @Id
    val jwtId: JwtId,
    val issueDate: OperDate,
    val validUntil: OperDate,
    val jwt: Jwt,
    val applicationId: ApplicationEnum,
    val issuedTo: String,
    val isRevoked: Boolean,
    val revokeDate: OperDateNull,
    val tag: String

) : AbstractJwt() {
    override fun getId(): JwtId = jwtId
}
