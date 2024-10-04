package org.dbs.service

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import org.dbs.consts.CKS
import org.dbs.consts.IpAddress
import org.dbs.consts.Jwt
import org.dbs.consts.MK
import org.dbs.ext.ResponseCoProcessorWrapper
import org.dbs.grpc.consts.GM
import org.dbs.grpc.consts.GMBuilder
import org.dbs.protobuf.core.ResponseAnswer
import org.dbs.spring.security.api.JwtSecurityServiceApi
import reactor.core.publisher.Mono

typealias JwtValidator = (JwtSecurityServiceApi, Jwt) -> MK
typealias GrpcServerAction = suspend () -> GM
typealias BadResponseAnswer<T> = () -> T
typealias RAB = ResponseAnswer.Builder
typealias RA = ResponseAnswer
typealias GrpcResponseBuilder<T> = suspend (RA) -> T
typealias EntityResponseBuilder<T> = suspend (RAB) -> T
typealias EntityResponseBuilder0<B, R> = suspend (RAB) -> ResponseCoProcessorWrapper<B, R>
typealias ProcessorWrapper<R, B> = suspend () -> ResponseCoProcessorWrapper<R, B>
typealias NoArg2RAB = () -> RAB
typealias GrpcResponse<GM> = (ResponseAnswer) -> GM
typealias MonoRAB = Mono<RAB>

interface GrpcServerServiceApi {

    val whiteProcs: Collection<CKS>
    val jwtValidator: JwtValidator
    val jwtSecurityService: JwtSecurityServiceApi

    fun validateRemoteAddress(ip: IpAddress)

    fun <ReqT, RespT> interceptCallInternal(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): Context
}
