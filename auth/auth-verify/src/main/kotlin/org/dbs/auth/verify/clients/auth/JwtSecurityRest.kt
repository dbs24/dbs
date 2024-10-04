package org.dbs.auth.verify.clients.auth

import org.dbs.auth.verify.clients.auth.value.VerifyJwtRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
@Lazy(false)
class JwtSecurityRest : H1_PROCESSOR<ServerRequest>() {
    //==========================================================================
    suspend fun verifyJwt(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        VerifyJwtRequest(it).buildResponse(this@JwtSecurityRest)
    }
}
