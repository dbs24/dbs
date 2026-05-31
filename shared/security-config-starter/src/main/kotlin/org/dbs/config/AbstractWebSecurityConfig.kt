package org.dbs.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_ADDITIONAL_PATH
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_CREDENTIALS
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_HEADERS
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_METHODS
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_MAX_AGE
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_NETWORK_CORS_ALLOWED_ADDITIONAL_PATH
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_CORS_CONFIG_ENABLED
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.consts.SysConst.STRING_ONE
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.ext.CollectionFuncs.ensureNoDuplicates
import org.dbs.ref.serv.enums.CountryEnum
import org.dbs.ref.serv.enums.CurrencyEnum
import org.dbs.ref.serv.enums.RegionEnum
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.service.ReqRespSuspend
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import java.time.Duration.ofHours
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis


@Suppress("UnusedPrivateMember")
@Deprecated("to remove")
abstract class AbstractWebSecurityConfig(private val appUriPrefix: String? = STRING_NULL) : MainApplicationConfig() {

    private val securityWebFilterChainConfig by lazy { LateInitVal<SecurityWebFilterChainConfig>("securityWebFilterChainConfig") }
    private val environment by lazy { LateInitVal<Environment>("environment") }

    private val endpointsAmount = AtomicInteger()

    @Autowired
    fun initBeans(
        securityWebFilterChainConfig: SecurityWebFilterChainConfig,
        environment: Environment,
    ) {
        this.securityWebFilterChainConfig.init(securityWebFilterChainConfig)
        this.environment.init(environment)
    }

    @Value("\${$NETWORK_CORS_ALLOWED_ADDITIONAL_PATH:$VALUE_NETWORK_CORS_ALLOWED_ADDITIONAL_PATH}")
    private val allowedOriginsAdditional = setOf("http://localhost:4200", "http://localhost:5173")

    @Value("\${$NETWORK_CORS_ALLOWED_HEADERS:*}")
    private val corsAllowedHeaders = EMPTY_STRING

    @Value("\${$NETWORK_CORS_ALLOWED_METHODS:*}")
    private val corsAllowedMethods = EMPTY_STRING

    @Value("\${$NETWORK_CORS_ALLOWED_CREDENTIALS:$STRING_FALSE}")
    private val corsAllowCredentials = false

    @Value("\${$NETWORK_CORS_MAX_AGE:$STRING_ONE}")
    private val corsMaxAge = 1L

    private val allowedOriginsDefault = setOf("http://127.0.0.1:8080")

    private val logRoute: (route: String, routeName: String) -> Unit = { route, routeName ->
        if (endpointsAmount.get() == 0) {
            logger.debug { "accept(APPLICATION_JSON)" }
            accept(APPLICATION_JSON)
        }
        logger.debug {
            "${endpointsAmount.incrementAndGet()}. " +
                    "${securityWebFilterChainConfig.value.getRouteStatus(route)} route $routeName $route"
        }
    }

    fun CoRouterFunctionDsl.addPostRoute(route: String, func: ReqRespSuspend) = POST(route, func)
        .also { logRoute(route, "POST") }

    fun CoRouterFunctionDsl.addGetRoute(route: String, func: ReqRespSuspend) = GET(route, func)
        .also { logRoute(route, "GET") }

    //==========================================================================

    @Order(HIGHEST_PRECEDENCE)
    @Bean
    @ConditionalOnProperty(name = [YML_CORS_CONFIG_ENABLED], havingValue = STRING_TRUE, matchIfMissing = true)
    open fun corsWebFilter() = CorsWebFilter(UrlBasedCorsConfigurationSource().apply {

        logger.debug { "initialize cors web filter: $allowedOriginsAdditional" }

        registerCorsConfiguration("/**", CorsConfiguration().apply {

            allowedOrigins = (allowedOriginsAdditional + allowedOriginsDefault).toList()
            addAllowedHeader(corsAllowedHeaders)
            addAllowedMethod(corsAllowedMethods)
            allowCredentials = corsAllowCredentials
            setMaxAge(ofHours(corsMaxAge))
        })
    })

    override fun initialize() = super.initialize().also {
        runBlocking(Dispatchers.IO) {
            measureTimeMillis {

                val roc = async {
                    // validate RestOperCode
                    RestOperCodeEnum.entries.toList()
                        .ensureNoDuplicates({ ::getCode }, { ::getValue })
                        .size

                }

                val fld = async {
                    // validate Field
                    Field.entries.toList()
                        .ensureNoDuplicates({ ::getCode }, { ::getValue })
                        .size
                }

                val errs = async {
                    // validate Error
                    Error.entries.toList()
                        .ensureNoDuplicates({ ::getCode }, { ::getValue })
                        .size
                }

                val country = async {
                    // validate Countries
                    CountryEnum.entries.toList()
                        .ensureNoDuplicates(
                            { ::getCountryId },
                            { ::getCountryIso },
                            { ::getCountryName },
                            { ::getCountryIso3 })
                        .size
                }

                val currency = async {
                    // validate Currency
                    CurrencyEnum.entries.toList()
                        .ensureNoDuplicates({ ::getCurrencyId }, { ::getCurrencyIso }, { ::getCurrencyName })
                        .size
                }

                val region = async {
                    // validate Region
                    RegionEnum.entries.toList()
                        .ensureNoDuplicates({ ::getRegionId }, { ::getRegionName })
                        .size
                }

//                val r2http = async {
//                    // RestOperCode2HttpEnum
//                    RestOperCode2HttpEnum.entries.toList()
//                        .ensureNoDuplicates({ ::httpCode })
//                        .size
//                }

                val records = /*r2http.await()*/ + region.await() + currency.await() + country.await() + errs.await() +
                        fld.await() + roc.await()

                logger.debug { "synchronize ref records: $records" }

            }.also {
                logger.debug { "synchronize RestOperCode, Field, Error, Country, Currency, Region: took $it ms" }
            }
        }
    }
}
