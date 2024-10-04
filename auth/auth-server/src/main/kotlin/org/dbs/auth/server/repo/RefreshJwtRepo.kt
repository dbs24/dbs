package org.dbs.auth.server.repo

import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.repo.sql.SQL_SELECT_REFRESH_JWT
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Mono

interface RefreshJwtRepo : R2dbcRepository<RefreshJwt, JwtId> {
    @Query(SQL_SELECT_REFRESH_JWT)
    fun findByJwtUnique(@Param("JWT") jwt: Jwt): Mono<RefreshJwt>
}
