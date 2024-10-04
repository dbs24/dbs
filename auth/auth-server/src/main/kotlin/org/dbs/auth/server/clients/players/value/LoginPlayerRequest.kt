package org.dbs.auth.server.clients.players.value

import org.dbs.auth.server.clients.players.grpc.GrpcPlayerLogin.loginPlayerInternal
import org.dbs.auth.server.service.AuthServiceLayer.Companion.authServerGrpcService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.restFulService
import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.ServerRequestFuncs.userAgent
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.auth.server.clients.players.grpc.convert.PlayerLoginConverter as H1_CONV
import org.dbs.rest.dto.value.IssuedJwtResultDto as OUT_DTO
import org.dbs.rest.dto.value.LoginUserDto as IN_DTO
import org.dbs.rest.dto.value.LoginUserRequest as H1_REQ
import org.dbs.rest.dto.value.LoginUserResponse as H1_RES

@JvmInline
value class LoginPlayerRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<IN_DTO, H1_REQ, OUT_DTO, H1_RES> {
    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = processor.createResponse(
        serverRequest,
        H1_REQ::class,
        H1_RES::class
    ) {
        serverRequest.run {
            restFulService.buildMonoResponse(this, H1_REQ::class)
            {
                applyH1Converter(H1_CONV::class, it, id())
                { with(authServerGrpcService) { loginPlayerInternal(it, ip(), userAgent()) } }
            }
        }
    }
}
