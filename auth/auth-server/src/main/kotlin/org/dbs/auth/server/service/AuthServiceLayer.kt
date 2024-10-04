package org.dbs.auth.server.service

import org.dbs.auth.server.clients.players.PlayersSecurityService
import org.dbs.auth.server.clients.players.client.PlayerClientService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.component.JwtSecurityService
import org.dbs.rest.service.RestFulService
import org.dbs.spring.core.api.ServiceLocator.findService

interface AuthServiceLayer {

    companion object {
        val restFulService by lazy { findService(RestFulService::class) }
        val playerClientService by lazy { findService(PlayerClientService::class) }
        val playersSecurityService by lazy { findService(PlayersSecurityService::class) }
        val authServerGrpcService by lazy {findService(AuthServerGrpcService::class) }
        val jwtSecurityService by lazy { findService(JwtSecurityService::class) }
        val jwtStorageService by lazy { findService(JwtStorageService::class) }

    }
}
