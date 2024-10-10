package org.dbs.auth.server.clients.industrial

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.auth.server.consts.AuthServerConsts.URI.ROUTE_PLAYER_LOGIN
import org.dbs.auth.server.consts.AuthServerConsts.URI.ROUTE_PLAYER_REFRESH_JWT
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.REACTIVE_APPLICATION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_WEB_APP_TYPE
import org.dbs.consts.SysConst.APP_CHESS_COMMUNITY
import org.dbs.consts.SysConst.APP_INDUSTRIAL_GOODS
import org.dbs.consts.SysConst.SLASH
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@OpenAPIDefinition(
    info = Info(
        title = APP_CHESS_COMMUNITY,
        description = APP_CHESS_COMMUNITY
    ),
    servers = [Server(url = SLASH)]
)
@ConditionalOnProperty(name = [SPRING_BOOT_WEB_APP_TYPE], havingValue = REACTIVE_APPLICATION, matchIfMissing = true)
class UsersRestConfig(private val securityRest: UsersSecurityRest) : AbstractWebSecurityConfig(APP_INDUSTRIAL_GOODS) {

    @SwaggerUserOpenApiRoutesDefinitions
    @Bean
    fun routerUsersAuthorizationRest(): RouterFunction<ServerResponse> = coRouter {
        addPostRoute(ROUTE_PLAYER_LOGIN, securityRest::playerLogin)
        addPostRoute(ROUTE_PLAYER_REFRESH_JWT, securityRest::playerRefreshJwt)
    }
}
