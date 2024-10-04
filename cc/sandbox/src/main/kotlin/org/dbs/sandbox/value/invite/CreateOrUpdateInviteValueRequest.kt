package org.dbs.sandbox.value.invite

import org.dbs.grpc.api.H1h2
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.dbs.sandbox.service.ApplicationServiceGate.ServicesList.restFulService
import org.dbs.sandbox.service.ApplicationServiceGate.ServicesList.sandBoxGrpcService
import org.dbs.sandbox.service.grpc.GrpcCreateOrUpdateInvite.createOrUpdateInviteInternal
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.invite.dto.invite.CreateInviteResponse as H1_RES
import org.dbs.invite.dto.invite.CreateOrUpdateInviteDto as DTO
import org.dbs.invite.dto.invite.CreateOrUpdateInviteRequest as H1_REQ
import org.dbs.invite.dto.invite.CreatedInviteDto as OUT_DTO
import org.dbs.sandbox.service.grpc.h1.convert.CreateOrUpdateInviteConverter as H1_CONV

@JvmInline
value class CreateOrUpdateInviteValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<DTO, H1_REQ, OUT_DTO, H1_RES> {

    override suspend fun buildResponse(processor: H1_PROCESSOR<R>) =
        processor.createResponse(
        serverRequest,
            H1_REQ::class,
            H1_RES::class
        ) {
            serverRequest.run {
                restFulService.buildMonoResponse(this, H1_REQ::class)
                {
                    H1h2.applyH1Converter(H1_CONV::class, it, id())
                    { with(sandBoxGrpcService) { createOrUpdateInviteInternal(it, ip()) } }
                }
            }
        }
}
