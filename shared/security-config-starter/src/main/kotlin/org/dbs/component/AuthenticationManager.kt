package org.dbs.component

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.RestHttpConsts.DEFAULT_ACCESS_ROLE
import org.dbs.security.RestApiSecurityService
import org.dbs.security.jwt.Jwt
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.toMono

@Service
class AuthenticationManager(
    private val jwtSecurityService: JwtSecurityService,
    private val restApiSecurityService: RestApiSecurityService
) : AbstractApplicationService(),
    ReactiveAuthenticationManager {


    override fun authenticate(authentication: Authentication): Mono<Authentication> =
        authentication.credentials.toString().run {
            logger.debug("authenticate [${this.last15()}]")
            if (jwtSecurityService.validateJwt(this) && authentication.isAuthenticated) authentication.toMono()
            else empty()
        }

    private fun createDefaultAuthentication(authToken: String, isAuthenticated: Boolean): Authentication =
        createCollection<String>().run {
            if (isAuthenticated) {
                this.add(DEFAULT_ACCESS_ROLE)
            }
            val authorities = createCollection<GrantedAuthority>()
            this.stream().forEach { role -> authorities.add(SimpleGrantedAuthority(role as String)) }
            UsernamePasswordAuthenticationToken(authToken, authToken, authorities)
        }

    //==========================================================================
    fun createSecurityContext(jwt: Jwt): Mono<SecurityContext> =
        restApiSecurityService.isAuthorized(jwt)
            .flatMap { authenticate(createDefaultAuthentication(jwt.token, it)) }
            .map(::SecurityContextImpl)

}
