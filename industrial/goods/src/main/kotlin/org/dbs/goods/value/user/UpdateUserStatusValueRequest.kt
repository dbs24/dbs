package org.dbs.goods.value.user

import org.dbs.grpc.api.H1h2
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.userGrpcService
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.restFulService
import org.dbs.goods.service.grpc.GrpcUpdateUserStatus.updateUserStatusInternal
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.goods.service.grpc.h1.convert.UpdateUserStatusConverter as H1_CONV
import org.dbs.goods.dto.user.CreatedUserStatusDto as OUT_DTO
import org.dbs.goods.dto.user.UpdateUserStatusDto as DTO
import org.dbs.goods.dto.user.UpdateUserStatusRequest as H1_REQ
import org.dbs.goods.dto.user.UpdateUserStatusResponse as H1_RES

@JvmInline
value class UpdateUserStatusValueRequest<R : ServerRequest>(private val serverRequest: R) :
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
                { with(userGrpcService) { updateUserStatusInternal(it, ip()) } }
            }
        }
    }
}
