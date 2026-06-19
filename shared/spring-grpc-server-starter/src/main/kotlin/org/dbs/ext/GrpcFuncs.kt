package org.dbs.ext

import io.grpc.Grpc
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.grpc.Metadata.BINARY_BYTE_MARSHALLER
import io.grpc.ServerCall
import org.dbs.application.core.service.funcs.StringFuncs.ends
import org.dbs.consts.GrpcConsts.MetadataKeys.GRPC_USER_AGENT
import org.dbs.consts.GrpcConsts.MetadataKeys.X_REAL_IP
import org.dbs.consts.IpAddress
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.enums.I18NEnum
import org.dbs.enums.I18NEnum.GRPC_PROCEDURE_NAME_IS_NOT_ASSIGNED
import org.dbs.enums.I18NEnum.GRPC_REMOTE_ADDRESS_IS_NOT_ASSIGNED
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV4Regex
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.ipAddress
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.isValidIp
import org.dbs.service.I18NService.Companion.findI18nMessage

object GrpcFuncs {
    fun <T, V> ServerCall<T, V>.log() =
        "ServerCall: isReady: $isReady, " +
                "isCancelled: $isCancelled, " +
                "attributes: $attributes, " +
                "securityLevel: $securityLevel, " +
                "authority: $authority, " +
                "methodDescriptor: $methodDescriptor"

    fun Metadata.log() = "Metadata: ${
        keys().map {
            "$it: ${
                if (it.ends("-bin"))
                    this[Metadata.Key.of(it, BINARY_BYTE_MARSHALLER)]
                else
                    this[Metadata.Key.of(it, ASCII_STRING_MARSHALLER)]
            }"
        }
    }"


    fun <ReqT, RespT> ServerCall<ReqT, RespT>.getProcedureName(): String = methodDescriptor.bareMethodName
        ?.also {
            require(it.length > 3) { "${findI18nMessage(GRPC_PROCEDURE_NAME_IS_NOT_ASSIGNED)} " }
        } ?: error(findI18nMessage(GRPC_PROCEDURE_NAME_IS_NOT_ASSIGNED))

    fun <ReqT, RespT> ServerCall<ReqT, RespT>.getRemoteAddress(grpcProcedure: String, headers: Metadata): IpAddress =
        (headers[X_REAL_IP]?.replace(allowedIpV4Regex, EMPTY_STRING)
            ?: attributes[Grpc.TRANSPORT_ATTR_REMOTE_ADDR]?.toString())
            ?.let {
                return@let it.ipAddress()
            }
            ?.also {
                require(it.isValidIp())
                { "$grpcProcedure: ${findI18nMessage(GRPC_REMOTE_ADDRESS_IS_NOT_ASSIGNED, it)} " }
            } ?: error("$grpcProcedure: ${findI18nMessage(GRPC_REMOTE_ADDRESS_IS_NOT_ASSIGNED)} ")

    fun <ReqT, RespT> ServerCall<ReqT, RespT>.getUserAgent(grpcProcedure: String, headers: Metadata): IpAddress =
        headers[GRPC_USER_AGENT]?.also {
            require(it.isNotEmpty())
        } ?: error("$grpcProcedure: ${findI18nMessage(I18NEnum.GRPC_USER_AGENT_IS_NOT_ASSIGNED)} ")
    //} ?: "fakedUserAgent"

}
