package org.dbs.rest.service

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.RestHttpConsts.REMOTE_IP_KEY
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
@Order(HIGHEST_PRECEDENCE)
class RestIpFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val ip = exchange.request.headers.getFirst("X-Forwarded-For")
            ?.split(",")?.firstOrNull()?.trim()
            ?: exchange.request.remoteAddress?.address?.hostAddress
            ?: "unknown rest IP"

        return chain.filter(exchange)
            .contextWrite { ctx -> ctx.put(REMOTE_IP_KEY, ip) }
    }

}