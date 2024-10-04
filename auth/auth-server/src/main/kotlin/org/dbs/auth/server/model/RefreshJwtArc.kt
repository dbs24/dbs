package org.dbs.auth.server.model

import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tkn_refresh_jwt_arc")
data class RefreshJwtArc(
    @Id
    @Column("jwt_id")
    val jwtId: JwtId,

    @Column("issue_date")
    val issueDate: OperDate,

    @Column("valid_until")
    val validUntil: OperDate,

    @Column("refresh_date")
    val refreshDate: OperDate,

    @Column("jwt")
    val jwt: Jwt,

    @Column("parent_jwt_id")
    val parentJwtId: JwtId,

    @Column("is_revoked")
    val isRevoked: Boolean,

    @Column("revoke_date")
    val revokeDate: OperDateNull

) : AbstractJwt() {
    override fun getId(): JwtId = jwtId
}
