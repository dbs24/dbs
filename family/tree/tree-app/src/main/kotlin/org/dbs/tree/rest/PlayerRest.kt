package org.dbs.tree.rest

import org.dbs.rest.api.consts.H1_PROCESSOR
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest as R

@Service
class PlayerRest: H1_PROCESSOR<R>() {

//    suspend fun createOrUpdatePlayer(request: R) = request.doRequestCo {
//        CreateOrUpdatePlayerValueRequest(it).buildResponse(this@PlayerRest)
//    }
//
//    suspend fun updatePlayerStatus(request: R) = request.doRequestCo {
//        UpdatePlayerStatusValueRequest(it).buildResponse(this@PlayerRest)
//    }
//
//    suspend fun updatePlayerPassword(request: R) = request.doRequestCo {
//        UpdatePlayerPasswordValueRequest(it).buildResponse(this@PlayerRest)
//    }
//
//    suspend fun getPlayerCredentials(request: R) = request.doRequestCo {
//        GetPlayerCredentialsRequest(it).buildResponse(this@PlayerRest)
//    }
}
