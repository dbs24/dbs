package org.dbs.rest.service

import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.SLASH
import org.dbs.rest.api.consts.RestApiConst.Headers.X_FORWARDED_FOR
import org.dbs.rest.api.consts.RestApiConst.Headers.X_REAL_IP
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.HOST
import org.springframework.http.HttpHeaders.USER_AGENT
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

typealias ReqRespSuspend = suspend (ServerRequest) -> ServerResponse

object ServerHttpRequestFuncs {

    private val replaceSymbol = SLASH.toRegex()
    fun ServerHttpRequest.log() = let {
        val headers = it.headers
        val authorization = headers[AUTHORIZATION]
        "███ [h1, ${it.method}, ${it.path}, ${printQp(it.queryParams)}id=${it.id}, " +
                (headers[X_REAL_IP]?.let { ip -> "$X_REAL_IP=$ip," } ?: EMPTY_STRING) +
                (headers[X_FORWARDED_FOR]?.let { xff -> "$X_FORWARDED_FOR=$xff," } ?: EMPTY_STRING) +
                " User-Agent=${headers[USER_AGENT]}, Host=${headers[HOST]}" +
                "${authorization?.run { ", $AUTHORIZATION=[${get(0)?.last15()}]" } ?: EMPTY_STRING}]," +
                " [${
                    remoteAddress?.toString()?.replace(replaceSymbol, EMPTY_STRING) ?: EMPTY_STRING
                } -> ${localAddress?.toString()?.replace(replaceSymbol, EMPTY_STRING) ?: EMPTY_STRING}, $id]"
    }

    private fun printQp(mvp: MultiValueMap<String, String>): String = run {
        mvp.takeIf { it.isNotEmpty() }?.let {"QueryParams=$it, "} ?: EMPTY_STRING
    }
}
