package org.dbs.auth.server.repo

import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.repo.sql.SQL_SELECT_ACCESS_JWT
import org.dbs.auth.server.repo.sql.SQL_SELECT_ACTUAL_ACCESS_JWT
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Mono

interface IssuedJwtRepo : R2dbcRepository<IssuedJwt, JwtId> {

    @Query(SQL_SELECT_ACTUAL_ACCESS_JWT)
    fun findActualJwtUnique(@Param("JWT") jwt: Jwt): Mono<IssuedJwt>

    @Query(SQL_SELECT_ACCESS_JWT)
    fun findAccessJwt(@Param("JWT") jwt: Jwt): Mono<IssuedJwt>

    @Query("CALL sp_delete_deprecated_jwt(:DATE)")
    fun deleteDeprecatedJwt(@Param("DATE") deprecatedDate: OperDate): Mono<Void>

    @Query("CALL sp_arc_deprecated_jwt(:DATE)")
    fun arcDeprecatedJwt(@Param("DATE") deprecatedDate: OperDate): Mono<Void>

    @Query("CALL sp_revoke_jwt(:JWT_OWNER, :APP_ID)")
    fun revokeJwt(@Param("JWT_OWNER") jwtOwner: String, @Param("APP_ID") application: ApplicationEnum): Mono<Void>
}
