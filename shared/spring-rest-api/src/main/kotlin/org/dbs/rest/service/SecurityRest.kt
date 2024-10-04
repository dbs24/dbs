package org.dbs.rest.service

import org.dbs.rest.dto.login.CreatedLoginResponse
import org.dbs.rest.dto.registration.CreatedRegistrationResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error

@Service
@ConditionalOnProperty(
    name = ["config.restful.security.profile.name"],
    havingValue = "standard",
    matchIfMissing = true
)
class SecurityRest : AbstractMonoRestProcessor() {
    //==========================================================================
    fun login(serverRequest: ServerRequest) =
        createResponse(serverRequest, CreatedLoginResponse::class, ::doLogin)
    //==========================================================================
    fun doLogin(serverRequest: ServerRequest): Mono<CreatedLoginResponse> =
        error(RuntimeException("${javaClass.canonicalName}: Illegal call method: ${serverRequest.path()}"))
    //==========================================================================
    fun registry(serverRequest: ServerRequest) =
        createResponse(serverRequest, CreatedRegistrationResponse::class, ::doRegistry)
    //==========================================================================
    fun doRegistry(serverRequest: ServerRequest): Mono<CreatedRegistrationResponse> =
        error(RuntimeException("${javaClass.canonicalName}: Illegal call method: ${serverRequest.path()}"))
    //==========================================================================
}
