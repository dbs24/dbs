package org.dbs.auth.server.clients.industrial

import org.dbs.auth.server.clients.industrial.value.LoginUserRequest
import org.dbs.auth.server.clients.industrial.value.RefreshUserJwtRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
@Lazy(false)
class UsersSecurityRest : H1_PROCESSOR<ServerRequest>() {
    //==========================================================================
    suspend fun playerLogin(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        LoginUserRequest(it).buildResponse(this@UsersSecurityRest)
    }

    suspend fun playerRefreshJwt(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        RefreshUserJwtRequest(it).buildResponse(this@UsersSecurityRest)
    }
}
