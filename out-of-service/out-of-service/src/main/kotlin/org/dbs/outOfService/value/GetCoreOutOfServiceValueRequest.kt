package org.dbs.outOfService.value

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.outOfService.service.CoreOutOfServiceService
import org.dbs.outOfService.service.grpc.OutOfServiceGrpcService
import org.dbs.outOfService.service.grpc.h1.GrpcGetCoreOutOfService.getCoreOutOfServiceInternal
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.RestFulService
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.outOfService.service.grpc.h1.convert.GetCoreOutOfServiceConverter as H1_CONV
import org.dbs.outOfService.dto.GetCoreOutOfServiceResponse as H1_RES
import org.dbs.outOfService.dto.GetCoreOutOfServiceRequest as H1_REQ
import org.dbs.outOfService.dto.GetCoreOutOfServiceDto as OUT_DTO

@JvmInline
value class GetCoreOutOfServiceValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactiveGetRequest<OUT_DTO, H1_RES> {

    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = processor.createResponse(serverRequest, H1_RES::class)
    {
        serverRequest.run {
            restFulService.buildMonoResponse(this) {
                applyH1Converter(
                    H1_CONV::class,
                    H1_REQ(EMPTY_STRING), id()
                )
                { with(outOfServiceGrpcService) { getCoreOutOfServiceInternal(it, ip()) } }
            }
        }
    }

    companion object {
        val outOfServiceGrpcService by lazy { findService(OutOfServiceGrpcService::class) }
        val restFulService by lazy { findService(RestFulService::class) }
    }
}
