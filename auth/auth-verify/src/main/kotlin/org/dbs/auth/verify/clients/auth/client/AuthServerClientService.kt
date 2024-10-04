package org.dbs.auth.verify.clients.auth.client

import org.dbs.consts.Jwt
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.RAB
import org.dbs.spring.core.api.ServiceBean

interface AuthServerClientService : ServiceBean {
    fun introspectJwt(jwt: Jwt, answerBuilder: RAB): MainResponse
}