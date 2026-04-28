package org.dbs.mgmt.value.lobby

import org.dbs.grpc.api.H1h2
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.mgmtGrpcService
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.restFulService
import org.dbs.mgmt.service.grpc.GrpcUpdateLobbyStatus.updateLobbyStatusInternal
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.lobby.dto.CreatedLobbyStatusDto as OUT_DTO
import org.dbs.lobby.dto.UpdateLobbyStatusDto as DTO
import org.dbs.lobby.dto.UpdateLobbyStatusRequest as H1_REQ
import org.dbs.lobby.dto.UpdateLobbyStatusResponse as H1_RES
import org.dbs.mgmt.service.grpc.h1.convert.UpdateLobbyStatusConverter as H1_CONV

@JvmInline
value class UpdateLobbyStatusValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<DTO, H1_REQ, OUT_DTO, H1_RES> {

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
                H1h2.applyH1Converter(H1_CONV::class, it, id())
                { with(mgmtGrpcService) { updateLobbyStatusInternal(it, ip()) } }
            }
        }
    }
}
