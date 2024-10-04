package org.dbs.auth.server.clients.players.value

import org.dbs.auth.server.clients.players.grpc.GrpcPlayerRefreshJwt.playerRefreshJwtInternal
import org.dbs.auth.server.service.AuthServiceLayer.Companion.authServerGrpcService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.restFulService
import org.dbs.grpc.api.H1h2
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.dto.value.IssuedJwtResultDto
import org.dbs.rest.dto.value.JwtList
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.ServerRequestFuncs.userAgent
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.auth.server.clients.players.grpc.convert.PlayerRefreshJwtConverter as CONV
import org.dbs.rest.dto.value.RefreshJwtRequest as REQ
import org.dbs.rest.dto.value.RefreshJwtResponse as RESP

@JvmInline
value class RefreshPlayerJwtRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>,
    HttpReactivePostRequest<JwtList, REQ, IssuedJwtResultDto, RESP> {
    override suspend fun buildResponse(processor: H1_PROCESSOR<R>) = processor.createResponse(
        serverRequest,
        REQ::class,
        RESP::class
    ) {
        serverRequest.run {
            restFulService.buildMonoResponse(this, REQ::class)
            {
                H1h2.applyH1Converter(CONV::class, it, id())
                { with(authServerGrpcService) { playerRefreshJwtInternal(it, ip(), userAgent()) } }
            }
        }
    }
}
