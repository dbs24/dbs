package org.dbs.rest.service

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.rest.api.consts.RestApiConst.Headers.X_REAL_IP
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV4Regex
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.validIpV4
import org.springframework.web.server.ServerWebExchange

object ServerWebExchangeExt {
    fun ServerWebExchange.log() = request.let {
        "ServerWebExchange: [${it.id}, ${it.method}, ${it.uri}, ${it.headers.headerSet()}, ${it.queryParams}, " +
                "${it.remoteAddress}, ${it.cookies}, " +
                "formData[${formData.block()}]]"
    }

    fun ServerWebExchange.ip(): String =
        "${xRealIp() ?: request.remoteAddress?.address?.hostAddress ?: UNKNOWN}:${request.remoteAddress?.port ?: 0}".validIpV4()

    fun ServerWebExchange.xRealIp(): String? =
        request.headers.getFirst(X_REAL_IP)?.replace(allowedIpV4Regex, EMPTY_STRING)

}
