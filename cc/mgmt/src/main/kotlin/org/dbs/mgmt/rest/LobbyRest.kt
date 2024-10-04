package org.dbs.mgmt.rest

import org.dbs.mgmt.service.LobbyService
import org.dbs.mgmt.value.lobby.CreateOrUpdateLobbyValueRequest
import org.dbs.mgmt.value.lobby.UpdateLobbyStatusValueRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
class LobbyRest : H1_PROCESSOR<ServerRequest>() {
    suspend fun createOrUpdateLobby(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        CreateOrUpdateLobbyValueRequest(it).buildResponse(this@LobbyRest)
    }

    suspend fun updateLobbyStatus(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        UpdateLobbyStatusValueRequest(it).buildResponse(this@LobbyRest)
    }

}
