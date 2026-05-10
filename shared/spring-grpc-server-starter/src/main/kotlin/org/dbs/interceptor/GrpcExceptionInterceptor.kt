package org.dbs.interceptor

import io.grpc.ForwardingServerCall
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.IpAddress
import org.dbs.consts.MKB
import org.dbs.ext.GrpcFuncs.getProcedureName
import org.dbs.ext.GrpcFuncs.getRemoteAddress
import org.dbs.ext.GrpcFuncs.getUserAgent
import org.dbs.ext.GrpcFuncs.log
import org.dbs.validator.exception.ValidationException

@GrpcGlobalServerInterceptor
class GrpcExceptionInterceptor : ServerInterceptor, Logging {

    private fun translateException(e: Throwable): Pair<Status, Metadata> {

        val metadata = Metadata()
        logger.error(e.toString())
        e.printStackTrace()

        return when (e) {
            is ValidationException -> {

                e.errors.forEachIndexed { i, err ->
                    metadata.put(
                        MKB.of("error-$i-bin", Metadata.BINARY_BYTE_MARSHALLER),
                        err.toErrString().toByteArray())
                }
                Status.INVALID_ARGUMENT.withDescription("Validation failed").withCause(e) to metadata
            }
            else -> {

                metadata.put(MKB.of("internal-error-bin", Metadata.BINARY_BYTE_MARSHALLER), e.toString().toByteArray())
                Status.INTERNAL.withDescription("Internal server error: ${e.toString()}").withCause(e) to metadata
            }
        }.also {
            metadata.log()
        }
    }

    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {

        //val (grpcProcedure, remoteAddress, userAgent) = call.logH2Call(headers)
        call.logH2Call(headers)

        return next.startCall(object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            override fun close(status: Status, trailers: Metadata) {
                if (status.isOk) {
                    super.close(status, trailers)
                } else {
                    // Если пришла ошибка (в т.ч. из корутины), мапим её
                    val exception = status.cause
                    val (newStatus, newTrailers) = translateException(exception ?: status.asException())
                    super.close(newStatus, newTrailers)
                }
            }
        }, headers)
    }

    private fun <ReqT, RespT> ServerCall<ReqT, RespT>.logH2Call(headers: Metadata): Triple<String, IpAddress, String> = run {
        logger.info { "███ h2 request" }
        val grpcProcedure = getProcedureName()
        val remoteAddress = getRemoteAddress(grpcProcedure, headers)
        val userAgent = getUserAgent(grpcProcedure, headers)
        logger.info { "request from $authority($userAgent) [$remoteAddress] ==> [$grpcProcedure] " }
        logger.debug { headers.log() }
        logger.debug { log() }
        Triple(grpcProcedure, remoteAddress, userAgent)
    }
}
