package org.dbs.auth.service

import org.dbs.consts.Jwt
import org.dbs.protobuf.core.MainResponse

interface GrpcAuthServerClientService {
    fun introspectJwt(jwt: Jwt): MainResponse
}