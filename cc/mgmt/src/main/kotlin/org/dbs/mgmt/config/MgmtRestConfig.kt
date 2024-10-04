package org.dbs.mgmt.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.REACTIVE_APPLICATION
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_BOOT_WEB_APP_TYPE
import org.dbs.consts.SysConst.APP_CHESS_COMMUNITY
import org.dbs.consts.SysConst.APP_CC_CAMEL_CASE
import org.dbs.consts.SysConst.SLASH
import org.dbs.lobby.LobbyConsts.Routes.ROUTE_CREATE_OR_UPDATE_LOBBY
import org.dbs.lobby.LobbyConsts.Routes.ROUTE_UPDATE_LOBBY_STATUS
import org.dbs.mgmt.rest.LobbyRest
import org.dbs.mgmt.rest.PlayerRest
import org.dbs.player.PlayersConsts.Routes.ROUTE_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.PlayersConsts.Routes.ROUTE_GET_PLAYER_CREDENTIALS
import org.dbs.player.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_PASSWORD
import org.dbs.player.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_STATUS
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
        title = APP_CHESS_COMMUNITY,
        description = APP_CC_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
@ConditionalOnProperty(name = [SPRING_BOOT_WEB_APP_TYPE], havingValue = REACTIVE_APPLICATION, matchIfMissing = true)
class MgmtRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    fun routerSmartSafeSchoolRest(
        playerRest: PlayerRest,
        lobbyRest: LobbyRest,
    ): RouterFunction<ServerResponse> = coRouter {
        //addCommonRoutes()
        // Players
        addPostRoute(ROUTE_CREATE_OR_UPDATE_PLAYER, playerRest::createOrUpdatePlayer)
        addPostRoute(ROUTE_UPDATE_PLAYER_STATUS, playerRest::updatePlayerStatus)
        addPostRoute(ROUTE_UPDATE_PLAYER_PASSWORD, playerRest::updatePlayerPassword)
        addGetRoute(ROUTE_GET_PLAYER_CREDENTIALS, playerRest::getPlayerCredentials)
        // Lobbies
        addPostRoute(ROUTE_CREATE_OR_UPDATE_LOBBY, lobbyRest::createOrUpdateLobby)
        addPostRoute(ROUTE_UPDATE_LOBBY_STATUS, lobbyRest::updateLobbyStatus)
    }
}
