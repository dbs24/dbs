package org.dbs.auth.server.dao

import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.IssuedJwtArc
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.model.RefreshJwtArc
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import reactor.core.publisher.Mono

interface AuthServerDao {
    fun synchronizeRefs(): Long
    fun moveJwt2Arc(expiredIssuedJwt: IssuedJwt): Mono<IssuedJwtArc>
    fun moveRefreshJwt2Arc(obsoleteRefreshJwt: RefreshJwt): Mono<Void>
    fun deleteDeprecatedJwt(obsoleteRefreshJwtId: JwtId, expiredIssueJwtId: JwtId): Mono<Void>
    fun saveIssuedToken(issuedJwt: IssuedJwt): Mono<IssuedJwt>
    fun deleteIssuedToken(jwtId: JwtId): Mono<Void>
    fun createIssuedArcToken(issuedJwtArc: IssuedJwtArc): Mono<IssuedJwtArc>
    fun saveRefreshToken(refreshJwt: RefreshJwt): Mono<RefreshJwt>
    fun deleteRefreshToken(jwtId: JwtId): Mono<Void>
    fun createRefreshArcToken(refreshJwtArc: RefreshJwtArc): Mono<RefreshJwtArc>
    fun findAccessToken(jwt: Jwt): Mono<IssuedJwt>
    fun findActualToken(jwt: Jwt): Mono<IssuedJwt>
    fun findRefreshToken(jwt: Jwt): Mono<RefreshJwt>
    fun deleteDeprecatedJwt(deprecateDate: OperDate): Mono<Void>
    fun arcDeprecatedJwt(deprecateDate: OperDate): Mono<Void>
    fun revokeExistsJwt(jwtOwner: String, application: ApplicationEnum): Mono<Void>
}