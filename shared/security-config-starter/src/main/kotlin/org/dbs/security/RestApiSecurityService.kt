package org.dbs.security

import org.dbs.security.jwt.Jwt
import reactor.core.publisher.Mono

interface RestApiSecurityService {
    fun isAuthorized(jwt: Jwt): Mono<Boolean>
}
