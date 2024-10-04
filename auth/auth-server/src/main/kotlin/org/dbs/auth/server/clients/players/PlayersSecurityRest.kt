package org.dbs.auth.server.clients.players

import org.dbs.auth.server.clients.players.value.LoginPlayerRequest
import org.dbs.auth.server.clients.players.value.RefreshPlayerJwtRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
@Lazy(false)
class PlayersSecurityRest : H1_PROCESSOR<ServerRequest>() {
    //==========================================================================
    suspend fun playerLogin(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        LoginPlayerRequest(it).buildResponse(this@PlayersSecurityRest)
    }

    suspend fun playerRefreshJwt(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        RefreshPlayerJwtRequest(it).buildResponse(this@PlayersSecurityRest)
    }
}
