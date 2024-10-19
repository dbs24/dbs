package org.dbs.auth.verify.clients.auth.client.impl

import org.dbs.application.core.service.funcs.IntFuncs.errorS
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.service.GrpcAuthServerClientService
import org.dbs.auth.verify.clients.auth.client.AuthServerClientService
import org.dbs.consts.Jwt
import org.dbs.protobuf.core.MainResponse
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.importErrorsIfAny
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service

@Service
class AuthServerClientServiceImpl(private val grpcAuthServerClientService: GrpcAuthServerClientService) : AuthServerClientService, AbstractApplicationService() {

    override fun introspectJwt(
        jwt: Jwt,
        answerBuilder: RAB,
    ): MainResponse = grpcAuthServerClientService.introspectJwt(jwt).also {

        answerBuilder.apply {
            if (importErrorsIfAny(it.responseAnswer)) {
                logger.warn { "${jwt.last15()}: ${errorMessagesCount.errorS()}" }
            } else {
                // validate status
                logger.warn { "${jwt.last15()}: ${errorMessagesCount.errorS()}" }
            }
        }
    }
}
