package org.dbs.auth.verify.clients.auth.value

import org.dbs.auth.verify.clients.auth.grpc.GrpcJwtVerify.verifyJwtInternal
import org.dbs.auth.verify.service.AuthServiceLayer.Companion.jwtVerifyGrpcService
import org.dbs.auth.verify.service.AuthServiceLayer.Companion.restFulService
import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.ServerRequestFuncs.userAgent
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.auth.verify.clients.auth.grpc.convert.JwtVerifyConverter as H1_CONV
import org.dbs.auth.verify.clients.auth.dto.JwtVerifyResultDto as OUT_DTO
import org.dbs.auth.verify.clients.auth.dto.Jwt4VerifyDto as IN_DTO
import org.dbs.auth.verify.clients.auth.dto.VerifyJwtRequest as H1_REQ
import org.dbs.auth.verify.clients.auth.dto.VerifyJwtResponse as H1_RES

@JvmInline
value class VerifyJwtRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<IN_DTO, H1_REQ, OUT_DTO, H1_RES> {
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
                applyH1Converter(H1_CONV::class, it, id())
                { with(jwtVerifyGrpcService) { verifyJwtInternal(it, ip()) } }
            }
        }
    }
}
