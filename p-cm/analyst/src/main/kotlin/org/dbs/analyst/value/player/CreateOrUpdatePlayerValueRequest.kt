package org.dbs.analyst.value.player

import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.grpc.api.H1h2
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.dbs.analyst.service.PlayerService
import org.dbs.analyst.service.grpc.CmGrpcService
import org.dbs.analyst.service.grpc.GrpcCreateOrUpdatePlayer.createOrUpdatePlayerInternal
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.player.dto.player.CreatePlayerResponse as H1_RES
import org.dbs.player.dto.player.CreateOrUpdatePlayerDto as DTO
import org.dbs.player.dto.player.CreateOrUpdatePlayerRequest as H1_REQ
import org.dbs.player.dto.player.CreatedPlayerDto as OUT_DTO
import org.dbs.analyst.service.grpc.h1.convert.CreateOrUpdatePlayerConverter as H1_CONV

@JvmInline
value class CreateOrUpdatePlayerValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<DTO, H1_REQ, OUT_DTO, H1_RES> {

    override suspend fun buildResponse(processor: H1_PROCESSOR<R>, playerService: PlayerService) =
        processor.createResponseCo(
        serverRequest,
            H1_REQ::class,
            H1_RES::class
        ) {
            serverRequest.run {
                playerService.buildMonoResponse(this, H1_REQ::class)
                {
                    H1h2.applyH1Converter(H1_CONV::class, it, id())
                    { with(actorGrpcService) { createOrUpdatePlayerInternal(it, ip()) } }
                }
            }
        }

    companion object {
        val actorGrpcService by lazy { findService(CmGrpcService::class) }
    }
}
