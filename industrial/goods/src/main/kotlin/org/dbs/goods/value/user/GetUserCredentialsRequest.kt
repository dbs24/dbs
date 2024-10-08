package org.dbs.goods.value.user

import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.userGrpcService
import org.dbs.goods.service.ApplicationServiceGate.ServicesList.restFulService
import org.dbs.goods.service.grpc.GrpcGetUserCredentials.getUserCredentialsInternal
import org.dbs.goods.UsersConsts.CmQueryParams.QP_USER_LOGIN
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.ServerRequestFuncs.qp
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.goods.service.grpc.h1.convert.UserCredentialsConverter as H1_CONV
import org.dbs.goods.dto.user.GetUserCredentialsResponse as H1_RESP
import org.dbs.goods.dto.user.UserAuthDto as H1_DTO

@JvmInline
value class GetUserCredentialsRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactiveGetRequest<H1_DTO, H1_RESP> {

    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = serverRequest.run {
        processor.createResponse(this, H1_RESP::class) {
            restFulService.buildMonoResponse(this) {
                applyH1Converter(H1_CONV::class, qp(QP_USER_LOGIN), id())
                { with(userGrpcService) { getUserCredentialsInternal(it, ip()) } }
            }
        }
    }
}
