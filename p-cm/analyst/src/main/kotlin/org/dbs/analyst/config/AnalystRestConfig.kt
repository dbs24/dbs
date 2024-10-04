package org.dbs.analyst.config

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_REST_ROUTES_ENABLED
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.analyst.rest.PlayerRest
import org.dbs.analyst.rest.SolutionRest
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SysConst.APP_CM_CAMEL_CASE
import org.dbs.consts.SysConst.APP_CM
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.player.consts.PlayersConsts.Routes.CM_GET_SOLUTION
import org.dbs.player.consts.PlayersConsts.Routes.ROUTE_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.consts.PlayersConsts.Routes.ROUTE_GET_PLAYER_CREDENTIALS
import org.dbs.player.consts.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_STATUS
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
        title = APP_CM,
        description = APP_CM_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
class AnalystRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    @ConditionalOnProperty(name = [YML_REST_ROUTES_ENABLED], havingValue = STRING_TRUE, matchIfMissing = true)
    fun routerSmartSafeSchoolRest(
        playerRest: PlayerRest,
        solutionRest: SolutionRest,
    ): RouterFunction<ServerResponse> = coRouter {
        //addCommonRoutes()
        // Players
        addPostRoute(ROUTE_CREATE_OR_UPDATE_PLAYER, playerRest::createOrUpdatePlayer)
        addPostRoute(ROUTE_UPDATE_PLAYER_STATUS, playerRest::updatePlayerStatus)
        addGetRoute(ROUTE_GET_PLAYER_CREDENTIALS, playerRest::getPlayerCredentials)
        // Solution
        addGetRoute(CM_GET_SOLUTION, solutionRest::getSolution)
    }
}
