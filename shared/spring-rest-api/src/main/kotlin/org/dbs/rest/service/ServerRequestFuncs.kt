package org.dbs.rest.service

import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.consts.Jwt
import org.dbs.consts.QueryParamName
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.NOT_ASSIGNED
import org.dbs.consts.SysConst.NOT_DEFINED
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.rest.api.consts.RestApiConst.Headers.X_REAL_IP
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV4Regex
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.web.reactive.function.server.ServerRequest

object ServerRequestFuncs {
    fun ServerRequest.extractJwt(): Jwt =
        this.headers().firstHeader(AUTHORIZATION)?.getJwtFromBearer() ?: EMPTY_STRING

    fun ServerRequest.qp(queryParamName: QueryParamName): String = qpDef(queryParamName, NOT_DEFINED)

    fun ServerRequest.qpOrEmpty(queryParamName: QueryParamName): String = qpDef(queryParamName, EMPTY_STRING)
    fun ServerRequest.qpOrNull(queryParamName: QueryParamName): String? =
        this.queryParam(queryParamName).orElse(STRING_NULL)

    fun ServerRequest.userAgent(): String = headers().firstHeader(USER_AGENT) ?: NOT_ASSIGNED

    fun ServerRequest.ip(): String = if (remoteAddress().isEmpty) "localhost/test" else
        //"${xRealIp() ?: remoteAddress().get().address?.hostAddress ?: UNKNOWN}:${remoteAddress().get().port}"
        xRealIp() ?: remoteAddress().get().address?.hostAddress ?: UNKNOWN

    fun ServerRequest.xRealIp(): String? = headers().firstHeader(X_REAL_IP)?.replace(allowedIpV4Regex, EMPTY_STRING)

    fun ServerRequest.qpDef(queryParamName: QueryParamName, defaultValue: String): String =
        this.queryParam(queryParamName).orElse(defaultValue)

    fun ServerRequest.id(): String = this.exchange().request.id

    suspend inline fun ServerRequest.doRequestCo(crossinline processor: ReqRespSuspend) = processor(this)

}
