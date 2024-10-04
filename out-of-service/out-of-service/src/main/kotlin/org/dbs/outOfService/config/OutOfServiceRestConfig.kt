package org.dbs.outOfService.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_REST_ROUTES_ENABLED
import org.dbs.consts.SysConst.APP_OUT_OF_SERVICE
import org.dbs.consts.SysConst.APP_OUT_OF_SERVICE_CAMEL_CASE
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.outOfService.consts.OutOfServiceConsts.Routes.ROUTE_CREATE_OR_UPDATE_CORE_OUT_OF_SERVICE
import org.dbs.outOfService.consts.OutOfServiceConsts.Routes.ROUTE_GET_CORE_OUT_OF_SERVICE
import org.dbs.outOfService.rest.CoreOutOfServiceRest
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
        title = APP_OUT_OF_SERVICE,
        description = APP_OUT_OF_SERVICE_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
class OutOfServiceRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    @ConditionalOnProperty(name = [YML_REST_ROUTES_ENABLED], havingValue = STRING_TRUE, matchIfMissing = true)
    fun routerSmartSafeSchoolRest(
        coreOutOfServiceRest: CoreOutOfServiceRest
    ): RouterFunction<ServerResponse> = coRouter {
        // Core out of service
        addPostRoute(ROUTE_CREATE_OR_UPDATE_CORE_OUT_OF_SERVICE, coreOutOfServiceRest::createOrUpdateCoreOutOfService)
        addGetRoute(ROUTE_GET_CORE_OUT_OF_SERVICE, coreOutOfServiceRest::getCoreOutOfService)
    }
}
