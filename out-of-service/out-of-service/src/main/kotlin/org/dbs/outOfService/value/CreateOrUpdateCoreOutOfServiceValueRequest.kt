package org.dbs.outOfService.value

import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.outOfService.service.CoreOutOfServiceService
import org.dbs.outOfService.service.grpc.OutOfServiceGrpcService
import org.dbs.outOfService.service.grpc.h1.GrpcCreateOrUpdateCoreOutOfService.createOrUpdateCoreOutOfServiceInternal
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.RestFulService
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceDto as IN_DTO
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceRequest as H1_REQ
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceResponse as H1_RES
import org.dbs.outOfService.dto.CreatedCoreOutOfServiceRespDto as OUT_DTO
import org.dbs.outOfService.service.grpc.h1.convert.CreateOrUpdateCoreOutOfServiceConverter as H1_CONV

@JvmInline
value class CreateOrUpdateCoreOutOfServiceValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>,
    HttpReactivePostRequest<IN_DTO, H1_REQ, OUT_DTO, H1_RES> {
    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = processor.createResponse(
        serverRequest,
        H1_REQ::class,
        H1_RES::class
    ) {
        serverRequest.run {
            restFulService.buildMonoResponse(this, H1_REQ::class) {
                applyH1Converter(H1_CONV::class, it, id())
                { with(outOfGrpcService) { createOrUpdateCoreOutOfServiceInternal(it, ip()) } }
            }
        }
    }

    companion object {
        val restFulService by lazy { findService(RestFulService::class) }
        val outOfGrpcService by lazy { findService(OutOfServiceGrpcService::class) }
    }
}
