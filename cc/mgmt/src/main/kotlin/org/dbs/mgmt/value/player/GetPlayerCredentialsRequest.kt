package org.dbs.mgmt.value.player

import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.mgmtGrpcService
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.restFulService
import org.dbs.mgmt.service.grpc.GrpcGetPlayerCredentials.getPlayerCredentialsInternal
import org.dbs.player.PlayersConsts.CmQueryParams.QP_PLAYER_LOGIN
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.ServerRequestFuncs.qp
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.mgmt.service.grpc.h1.convert.PlayerCredentialsConverter as H1_CONV
import org.dbs.player.dto.player.GetPlayerCredentialsResponse as H1_RESP
import org.dbs.player.dto.player.PlayerAuthDto as H1_DTO

@JvmInline
value class GetPlayerCredentialsRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactiveGetRequest<H1_DTO, H1_RESP> {

    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = serverRequest.run {
        processor.createResponse(this, H1_RESP::class) {
            restFulService.buildMonoResponse(this) {
                applyH1Converter(H1_CONV::class, qp(QP_PLAYER_LOGIN), id())
                { with(mgmtGrpcService) { getPlayerCredentialsInternal(it, ip()) } }
            }
        }
    }
}
