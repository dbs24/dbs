package org.dbs.analyst.value.player

import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.qp
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.dbs.analyst.service.PlayerService
import org.dbs.analyst.service.grpc.CmGrpcService
import org.dbs.analyst.service.grpc.GrpcGetPlayerCredentials.getPlayerCredentialsInternal
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_PLAYER_LOGIN
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.player.dto.player.GetPlayerCredentialsResponse as H1_RESP
import org.dbs.player.dto.player.PlayerAuthDto as H1_DTO
import org.dbs.analyst.service.grpc.h1.convert.PlayerCredentialsConverter as H1_CONV

@JvmInline
value class GetPlayerCredentialsRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactiveGetRequest<H1_DTO, H1_RESP> {

    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = serverRequest.run {
        processor.createResponseCo(this, H1_RESP::class.java) {
            playerService.buildMonoResponse(this) {
                applyH1Converter(H1_CONV::class, qp(QP_PLAYER_LOGIN), id())
                { with(actorGrpcService) { getPlayerCredentialsInternal(it, ip(), userAgent()) } }
            }
        }
    }

    companion object {
        val playerService by lazy { findService(PlayerService::class) }
        val actorGrpcService by lazy { findService(CmGrpcService::class) }
    }
}
