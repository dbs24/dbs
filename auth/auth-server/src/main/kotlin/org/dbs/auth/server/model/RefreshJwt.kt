package org.dbs.auth.server.model

import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tkn_refresh_jwt")
data class RefreshJwt(
    @Id
    val jwtId: JwtId,
    val issueDate: OperDate,
    val validUntil: OperDate,
    val jwt: Jwt,
    val parentJwtId: JwtId,
    val isRevoked: Boolean,
    val revokeDate: OperDateNull
) : AbstractJwt() {
    override fun getId() = jwtId
}
