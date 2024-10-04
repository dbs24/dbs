package org.dbs.component

import io.jsonwebtoken.ExpiredJwtException
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.service.funcs.Patterns.LEGAL_DOMAIN
import org.dbs.consts.SecurityConsts.PASSWORD_ENCODER_STRENGTH_DEF
import org.dbs.consts.SysConst.ONE_SECOND
import org.dbs.ext.LoggerFuncs.logException
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.subnet2IpV4
import org.dbs.rest.service.Bucket4jRateLimitService
import org.dbs.rest.service.ServerWebExchangeExt.ip
import org.dbs.rest.service.ServerWebExchangeExt.log
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.context.annotation.Bean
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

typealias JwtValidator = suspend (ServerWebExchange) -> Mono<SecurityContext>
typealias FuckOff2Ban = (ServerWebExchange) -> String

@Service
class SecurityContextRepository(
    private val authenticationManager: AuthenticationManager,
    private val jwtSecurityService: JwtSecurityService,
) : AbstractApplicationService(), ServerSecurityContextRepository {

    private val bucket4jRateLimitService by lazy { findService(Bucket4jRateLimitService::class) }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(PASSWORD_ENCODER_STRENGTH_DEF)

    override fun save(serverWebExchange: ServerWebExchange, sc: SecurityContext): Mono<Void> =
        error(UnsupportedOperationException("Not supported yet."))

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> =
        runBlocking { jwtValidator(serverWebExchange) }

    private val fuckOff2Ban: FuckOff2Ban = {
        it.ip().subnet2IpV4().also { bucket4jRateLimitService.addNewTemporaryBlackList(it) }
    }

    private val jwtValidator: JwtValidator = { swe ->

        if (swe.request.uri.path.endsWith("/") || !LEGAL_DOMAIN.matcher(swe.request.uri.host).matches()) {
            val fakedSubNet = fuckOff2Ban(swe)
            logger.warn { "Abused connection to ${swe.request.uri.path} from $fakedSubNet, ${swe.log()}" }
            empty()
        } else
            swe.toMono()
                .publishOn(boundedElastic)
                .flatMap {
                    jwtSecurityService.run {
                        extractJwt(it)?.let { jwt ->
                            checkToken(jwt)
                                ?.let {
                                    checkIp(jwt)
                                        ?.let { checkUserAgent(jwt) }
                                        ?.let { authenticationManager.createSecurityContext(jwt) }
                                }
                        } ?: empty()
                    }
                }
                .onErrorResume { processOnErrorResume(it, swe) }
                .switchIfEmpty {
                    runCatching {
                        jwtSecurityService.extractJwt(swe) ?: run {
                            fuckOff2Ban(swe)
                            logger.warn { "Jwt is missing, ${swe.log()}" }
                        }
                    }.getOrNull()
                    empty()
                }
                .cache(ONE_SECOND)
    }

    private fun processOnErrorResume(t: Throwable, swe: ServerWebExchange): Mono<SecurityContext> =
        t.run {
            logger.logException(this, ExpiredJwtException::class.java)
            if (t !is ExpiredJwtException) {
                logger.error { "$t: ${swe.log()}" }
            }
            empty()
        }
}
