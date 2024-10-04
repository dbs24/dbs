package org.dbs.security

import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.security.jwt.Jwt
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@Service
@Primary
@ConditionalOnMissingBean(name = ["restApiSecurityServiceImpl"])
class DefaultNonSecuredService : AbstractApplicationService(), RestApiSecurityService {

    val trueMono by lazy { true.toMono() }
    override fun isAuthorized(jwt: Jwt): Mono<Boolean> = trueMono
    override fun initialize() = super.initialize().also {
        logger.warn("Initialize non secured service")
    }
}
