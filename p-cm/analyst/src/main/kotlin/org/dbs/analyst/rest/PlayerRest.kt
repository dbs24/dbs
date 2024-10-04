package org.dbs.analyst.rest

import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequest
import org.dbs.analyst.service.PlayerService
import org.dbs.analyst.value.player.CreateOrUpdatePlayerValueRequest
import org.dbs.analyst.value.player.GetPlayerCredentialsRequest
import org.dbs.analyst.value.player.UpdatePlayerStatusValueRequest
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
class PlayerRest(
    private val playerService: PlayerService,
) : H1_PROCESSOR<ServerRequest>() {
    suspend fun createOrUpdatePlayer(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        CreateOrUpdatePlayerValueRequest(it).buildResponse(this@PlayerRest, playerService)
    }

    suspend fun updatePlayerStatus(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        UpdatePlayerStatusValueRequest(it).buildResponse(this@PlayerRest, playerService)
    }
    suspend fun getPlayerCredentials(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        GetPlayerCredentialsRequest(it).buildResponse(this@PlayerRest)
    }
}
