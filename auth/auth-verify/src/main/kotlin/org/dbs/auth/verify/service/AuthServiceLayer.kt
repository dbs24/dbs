package org.dbs.auth.verify.service

import org.dbs.auth.verify.clients.auth.client.AuthServerClientService
import org.dbs.auth.verify.service.grpc.JwtVerifyGrpcService
import org.dbs.rest.service.RestFulService
import org.dbs.spring.core.api.ServiceLocator.findService

interface AuthServiceLayer {

    companion object {
        val restFulService by lazy { findService(RestFulService::class) }
        val authServerClientService by lazy { findService(AuthServerClientService::class) }
        val jwtVerifyGrpcService by lazy {findService(JwtVerifyGrpcService::class) }
    }
}
