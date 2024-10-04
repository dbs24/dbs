package org.dbs.rest.service

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.GetNetworkAddress.currentHostName
import org.dbs.application.core.service.funcs.Patterns.LEGAL_DOMAIN
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SECURITY
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.MASK_ALL
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.ext.LoggerFuncs.measureExecTime
import org.dbs.rest.service.AbusedConnectionWebFilter.AbuseReasonEnum.AR_ACTUATOR
import org.dbs.rest.service.AbusedConnectionWebFilter.AbuseReasonEnum.AR_EMPTY_HOST
import org.dbs.rest.service.AbusedConnectionWebFilter.AbuseReasonEnum.AR_ILLEGAL_HEADER
import org.dbs.rest.service.AbusedConnectionWebFilter.AbuseReasonEnum.AR_ILLEGAL_HOST
import org.dbs.rest.service.AbusedConnectionWebFilter.AbuseReasonEnum.AR_ILLEGAL_QUERY_PARAM
import org.dbs.rest.service.ServerHttpRequestFuncs.log
import org.dbs.rest.service.ServerWebExchangeExt.log
import org.dbs.rest.service.WebSessionFuncs.logAndInvalidate
import org.dbs.service.HeaderService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Service
@ConditionalOnProperty(name = [JUNIT_MODE], havingValue = STRING_FALSE, matchIfMissing = true)
@ConfigurationProperties(CONFIG_SECURITY)
class AbusedConnectionWebFilter(val headerService: HeaderService) : AbstractApplicationService(), WebFilter {
//    val hosts: StringMap = createMap()

    internal enum class AbuseReasonEnum {
        AR_ILLEGAL_HOST,
        AR_EMPTY_HOST,
        AR_ACTUATOR,
        AR_ILLEGAL_HEADER,
        AR_ILLEGAL_QUERY_PARAM
    }

    internal data class AbusedConnection(private val reason: AbuseReasonEnum, private val detail: String)


    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> =
        serverWebExchange.run {
            logger.info { request.log() }

            val abusedConnection by lazy { LateInitVal<AbusedConnection>() }
            val ip = (request.remoteAddress?.address?.toString()?.replace(SLASH.toRegex(), EMPTY_STRING) ?: UNKNOWN)

            request.uri.host?.let {

                if (headerService.env.whiteHosts != MASK_ALL) {
                    // legal domain name
                    if (!LEGAL_DOMAIN.matcher(request.uri.host).matches()) {
                        abusedConnection.init(
                            AbusedConnection(
                                AR_ILLEGAL_HOST,
                                "$currentHostName: Suspicious host in request: ${request.uri.host}), " +
                                        "allowed hosts: [${headerService.whiteHostsLists}])"
                            )
                        )
                    } else {

                        // unknown host
                        if (!headerService.whiteHostsLists.contains(request.uri.host)) {
                            val errMsg = "$currentHostName: Suspicious host in request: ${request.uri.host}), " +
                                    "allowed hosts: [${headerService.whiteHostsLists}])"
                            if (headerService.env.breakIllegalHost) {
                                abusedConnection.update(
                                    AbusedConnection(AR_ILLEGAL_HOST, errMsg)
                                )
                            } else
                                logger.warn { errMsg }
                        }
                    }
                }

            } ?: run {
                abusedConnection.init(AbusedConnection(AR_EMPTY_HOST, "Host is not specified in header"))
            }

            // temporary
            if (request.path.toString().contains("/actuator/gateway")) {
                abusedConnection.init(AbusedConnection(AR_ACTUATOR, "abused connection to actuator"))
            }

            // White headers
            if (abusedConnection.isNotInitialized() && headerService.env.whiteHeaders != MASK_ALL) {
                logger.measureExecTime("headers white list filter") {
                    request.headers.asSequence().all { header ->
                        headerService.whiteHeadersLists.contains(header.key.lowercase()).also {
                            if (!it)
                                if (headerService.env.breakIllegalHeader) {
                                    abusedConnection.update(
                                        AbusedConnection(
                                            AR_ILLEGAL_HEADER,
                                            "$currentHostName: Illegal header: ${header.key}): $header)"
                                        )
                                    )
                                } else
                                    logger.warn {
                                        "illegal header in request (${header.key}): $header)" +
                                                ", all headers: [${request.headers}]"
                                    }
                        }
                    }
                }
            }

            // Query params
            if (abusedConnection.isNotInitialized() && headerService.env.abusedQueryParamsValues.isNotEmpty()) {
                request.queryParams.entries.firstOrNull { qp ->
                    headerService.env.abusedQueryParamsValues.any { qp.value.toString().contains(it) }
                }?.apply {
                    abusedConnection.update(
                        AbusedConnection(
                            AR_ILLEGAL_QUERY_PARAM,
                            "Illegal query param value: $this})"
                        )
                    )
                }
            }

            if (abusedConnection.isInitialized()) {
                logger.error { "### Abused connection from $ip: [${abusedConnection.value}], (${log()})" }
                response.statusCode = FORBIDDEN
                // invalidate session
                session.logAndInvalidate(logger)
            }
            webFilterChain.filter(this)
        }
}
