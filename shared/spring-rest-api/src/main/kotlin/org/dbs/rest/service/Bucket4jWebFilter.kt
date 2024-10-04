package org.dbs.rest.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.rest.service.ServerWebExchangeExt.ip
import org.dbs.rest.service.WebSessionFuncs.logAndInvalidate
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.validator.Error.USER_ACCESS_DENIED
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Service
@ConditionalOnProperty(name = [BUCKET_4J_ENABLED], havingValue = STRING_TRUE)
class Bucket4jWebFilter(
    private val bucket4jService: Bucket4jRateLimitService,
) : AbstractApplicationService(), WebFilter {
    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> =
        serverWebExchange.run {
            if (!bucket4jService.validateRateLimit(ip())
            ) {
                mutate()
                    .request { it.headers { hs -> hs.add(USER_ACCESS_DENIED.name, "Too many requests or access denied") } }
                    .build()
                session.logAndInvalidate(logger)
            }
            webFilterChain.filter(this)
        }
}
