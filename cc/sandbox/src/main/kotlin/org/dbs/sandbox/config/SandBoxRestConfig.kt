package org.dbs.sandbox.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.REACTIVE_APPLICATION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_WEB_APP_TYPE
import org.dbs.consts.SysConst.APP_SAND_BOX
import org.dbs.consts.SysConst.APP_SAND_BOX_CASE
import org.dbs.consts.SysConst.SLASH
import org.dbs.invite.InviteConsts.Routes.ROUTE_CREATE_OR_UPDATE_INVITE
import org.dbs.invite.InviteConsts.Routes.ROUTE_UPDATE_INVITE_STATUS
import org.dbs.sandbox.rest.InviteRest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@Primary
@OpenAPIDefinition(
    info = Info(
        title = APP_SAND_BOX,
        description = APP_SAND_BOX_CASE
    ),
    servers = [Server(url = SLASH)]
)
@ConditionalOnProperty(name = [SPRING_BOOT_WEB_APP_TYPE], havingValue = REACTIVE_APPLICATION, matchIfMissing = true)
class SandBoxRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    fun routerSandBoxRest(
        inviteRest: InviteRest,
    ): RouterFunction<ServerResponse> = coRouter {
        //addCommonRoutes()
        // Invites
        addPostRoute(ROUTE_CREATE_OR_UPDATE_INVITE, inviteRest::createOrUpdateInvite)
        addPostRoute(ROUTE_UPDATE_INVITE_STATUS, inviteRest::updateInviteStatus)

//        addPostRoute(ROUTE_UPDATE_PLAYER_STATUS, inviteRest::updatePlayerStatus)
//        addPostRoute(ROUTE_UPDATE_PLAYER_PASSWORD, inviteRest::updatePlayerPassword)
//        addGetRoute(ROUTE_GET_PLAYER_CREDENTIALS, inviteRest::getPlayerCredentials)
    }
}
