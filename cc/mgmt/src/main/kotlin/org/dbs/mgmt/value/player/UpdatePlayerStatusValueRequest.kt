package org.dbs.mgmt.value.player

import org.dbs.grpc.api.H1h2
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.mgmtGrpcService
import org.dbs.mgmt.service.ApplicationServiceGate.ServicesList.restFulService
import org.dbs.mgmt.service.grpc.GrpcUpdatePlayerStatus.updatePlayerStatusInternal
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.mgmt.service.grpc.h1.convert.UpdatePlayerStatusConverter as H1_CONV
import org.dbs.player.dto.player.CreatedPlayerStatusDto as OUT_DTO
import org.dbs.player.dto.player.UpdatePlayerStatusDto as DTO
import org.dbs.player.dto.player.UpdatePlayerStatusRequest as H1_REQ
import org.dbs.player.dto.player.UpdatePlayerStatusResponse as H1_RES

@JvmInline
value class UpdatePlayerStatusValueRequest<R : ServerRequest>(private val serverRequest: R) :
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
                { with(mgmtGrpcService) { updatePlayerStatusInternal(it, ip()) } }
            }
        }
    }
}
