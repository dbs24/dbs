package org.dbs.goods.config

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
import org.dbs.goods.rest.UserRest
import org.dbs.goods.UsersConsts.Routes.ROUTE_CREATE_OR_UPDATE_USER
import org.dbs.goods.UsersConsts.Routes.ROUTE_GET_USER_CREDENTIALS
import org.dbs.goods.UsersConsts.Routes.ROUTE_UPDATE_USER_PASSWORD
import org.dbs.goods.UsersConsts.Routes.ROUTE_UPDATE_USER_STATUS
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
class GoodsRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    fun routerSmartSafeSchoolRest(
        userRest: UserRest,
    ): RouterFunction<ServerResponse> = coRouter {
        //addCommonRoutes()
        // Users
        addPostRoute(ROUTE_CREATE_OR_UPDATE_USER, userRest::createOrUpdateUser)
        addPostRoute(ROUTE_UPDATE_USER_STATUS, userRest::updateUserStatus)
        addPostRoute(ROUTE_UPDATE_USER_PASSWORD, userRest::updateUserPassword)
        addGetRoute(ROUTE_GET_USER_CREDENTIALS, userRest::getUserCredentials)
    }
}
