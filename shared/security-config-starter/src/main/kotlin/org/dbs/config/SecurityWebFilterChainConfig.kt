package org.dbs.config

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.component.AuthenticationManager
import org.dbs.component.SecurityContextRepository
import org.dbs.consts.RestHttpConsts.DEFAULT_ACCESS_ROLE
import org.dbs.consts.RestHttpConsts.ROUTE_URI_LIVENESS
import org.dbs.consts.RestHttpConsts.ROUTE_URI_READINESS
import org.dbs.consts.RestHttpConsts.URI_API
import org.dbs.consts.RestHttpConsts.URI_EUREKA
import org.dbs.consts.RestHttpConsts.URI_EUREKA_HEALTH
import org.dbs.consts.RestHttpConsts.URI_EUREKA_INFO
import org.dbs.consts.RestHttpConsts.URI_LOGIN_API
import org.dbs.consts.RestHttpConsts.URI_REFRESH_JWT
import org.dbs.consts.RestHttpConsts.URI_SPRING_BOOT_ACTUATOR
import org.dbs.consts.RestHttpConsts.URI_STARTED
import org.dbs.consts.RestHttpConsts.URI_SWAGGER_API_DOCS
import org.dbs.consts.RestHttpConsts.URI_SWAGGER_LINKS
import org.dbs.consts.RestHttpConsts.URI_SWAGGER_MAIN
import org.dbs.consts.RestHttpConsts.URI_SWAGGER_WEBJARS_LINKS
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.SERVER_SSL_ENABLED_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST
import org.dbs.consts.SysConst.K8S_MODE
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.ext.LoggerFuncs.logException
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono.fromRunnable
import java.util.function.Consumer

@Service
class SecurityWebFilterChainConfig(
    private val authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
    @Value("\${$CONFIG_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST:$VALUE_SECURITY_PROFILE_ADVANCED_WHITE_URI_LIST}")
    val advancedUriList: Collection<String>,
    @Value("\${$SERVER_SSL_ENABLED:$SERVER_SSL_ENABLED_VALUE}")
    val ssl: Boolean = true
) : AbstractApplicationConfiguration() {
    // advanced uri

    // white list
    val whiteListAuthUrl by lazy {

            val defaultWhiteListAuthUrl = arrayOf(
                URI_STARTED,
                URI_LOGIN_API,
                URI_REFRESH_JWT,
                ROUTE_URI_LIVENESS,
                ROUTE_URI_READINESS,
                URI_SWAGGER_LINKS,
                URI_SWAGGER_WEBJARS_LINKS,
                URI_SWAGGER_API_DOCS,
                URI_SWAGGER_MAIN,
                URI_SPRING_BOOT_ACTUATOR,
                URI_EUREKA_INFO,
                URI_EUREKA_HEALTH,
                URI_EUREKA
            )

            createCollection<String>().also { whiteUrls ->
                val workWhiteUrls = createCollection<String>()
                workWhiteUrls.addAll(listOf(*defaultWhiteListAuthUrl))
                // advanced uri
                workWhiteUrls.addAll(advancedUriList)
                // white list
                workWhiteUrls.forEach(Consumer { whiteUrls.add(buildNewRoute(it, STRING_NULL)) })
            }
        }

    fun buildNewRoute(route: String, appUriPrefix: String?) = appUriPrefix?.run {
        //logger.debug("route = $route; appUriPrefix = $appUriPrefix")
        if (route.contains(this)) route else
            route.replace(
                URI_API + SLASH.toRegex(),
                "$URI_API/$appUriPrefix/"
            )
    } ?: route

    //==========================================================================
    fun getRouteStatus(route: String) =
        if (whiteListAuthUrl.stream().filter { it == route }.count() > 0) "*" else "\u00a9"

    //==========================================================================
    @Bean
    @ConditionalOnProperty(name = [JUNIT_MODE], havingValue = STRING_TRUE)
    fun junitSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http.run {
        logger.debug("junitSecurityWebFilterChain is activated")
        cors { it.disable() }
            .csrf { it.disable() }
            .authorizeExchange { it.anyExchange().permitAll() }
            .formLogin() { it.disable() }
            .build()
    }

    //==========================================================================
    private fun defaultFilterChain(http: ServerHttpSecurity, isSsl: Boolean) =
        (if (isSsl) http.redirectToHttps(withDefaults()) else http)
            .exceptionHandling {
                it.authenticationEntryPoint { swe: ServerWebExchange, ae: AuthenticationException ->
                    fromRunnable { processAuthenticationException(swe, ae, UNAUTHORIZED) }
                }.accessDeniedHandler { swe: ServerWebExchange, ae: AccessDeniedException ->
                    fromRunnable { processAuthenticationException(swe, ae, FORBIDDEN) }
                }
            }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authenticationManager(authenticationManager as ReactiveAuthenticationManager)
            .securityContextRepository(securityContextRepository as ServerSecurityContextRepository)
            .authorizeExchange {
                it.pathMatchers(*whiteListAuthUrl.toTypedArray()).permitAll()
                    .pathMatchers(OPTIONS).permitAll() // allow CORS options calls
                    .anyExchange().hasAnyAuthority(DEFAULT_ACCESS_ROLE)
            }
            .build()

    @Bean
    @ConditionalOnProperty(name = [K8S_MODE], havingValue = STRING_TRUE)
    fun k8sSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        defaultFilterChain(http, ssl).also {
            logger.debug("SSL=$ssl, k8sSecurityWebFilterChain is activated, [whiteUrls = $whiteListAuthUrl]")
        }

    //==========================================================================
    @Bean
    @ConditionalOnProperty(name = [K8S_MODE], havingValue = "false", matchIfMissing = true)
    fun defaultSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        defaultFilterChain(http, ssl).also {
            logger.debug("SSL=$ssl, defaultSecurityWebFilterChain is activated [whiteUrls = $whiteListAuthUrl]")
        }

    private fun processAuthenticationException(
        serverWebExchange: ServerWebExchange,
        runtimeException: RuntimeException,
        httpStatus: HttpStatus
    ) = serverWebExchange.apply {
        logger.logException(runtimeException, AuthenticationCredentialsNotFoundException::class.java)
        response.statusCode = httpStatus
    }
}
