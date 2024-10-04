package org.dbs.spring.core.api

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory.INSTANCE
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.nullsafe.StopWatcher
import org.dbs.consts.RestHttpConsts.ONE_ATTEMPT
import org.dbs.consts.RestHttpConsts.URI_HTTP
import org.dbs.consts.RestHttpConsts.URI_HTTPS
import org.dbs.consts.RouteUrl
import org.dbs.consts.SpringCoreConst.EMPTY_HTTP_HEADERS
import org.dbs.consts.SpringCoreConst.WebClientConsts.EMPTY_STATUS_PROCESSOR
import org.dbs.consts.SpringCoreConst.WebClientConsts.UNLIMITED_BUFFER
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.TIMEOUT_10000_MILLIS
import org.dbs.consts.WebClientOnStatusProcessor
import org.dbs.consts.WebClientUriBuilder
import org.dbs.ext.LoggerFuncs.logRequestInternal
import org.dbs.ext.LoggerFuncs.measureExecTime
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.MULTIPART_FORM_DATA
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.error
import reactor.core.publisher.Mono.just
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.tcp.SslProvider
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

typealias WebClientCall<R> = () -> Mono<R>

abstract class AbstractWebClientService : AbstractApplicationService() {

    private val serverUri by lazy { LateInitVal<String>() }
    private val initialUri by lazy { LateInitVal<String>() }
    override val env by lazy { findService(ApplicationYmlService::class) }

    private val isClientError = Predicate { httpStatusCode: HttpStatusCode ->
        httpStatusCode.value() == NOT_ACCEPTABLE.value()
    }

    val webClient: WebClient by lazy {
        (setupClientConnector(WebClient.builder(), serverUri.value)
            .baseUrl(serverUri.value)
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs {
                        it.defaultCodecs()
                            .maxInMemorySize(UNLIMITED_BUFFER)
                    }
                    .build())
            .build())
            .also {
                logger.debug(
                    "${javaClass.simpleName}: webClient was created ($serverUri ${
                        if (serverUri != initialUri)
                            "[$initialUri]" else EMPTY_STRING
                    })"
                )
            }
    }

    protected fun prepareDefaultWebClient(uriBase: String) = uriBase.let {
        initialUri.init(it)
        serverUri.init(
            (if (!it.startsWith(URI_HTTPS)) {
                if (it.startsWith(URI_HTTP)) it else URI_HTTPS + it
            } else it).trim()
        )

        logger.debug {
            "prepare default web client: ($serverUri " +
                    "${if (serverUri != initialUri) "($initialUri)" else EMPTY_STRING})"
        }

        addUrl4LivenessTracking(serverUri.value, javaClass.simpleName)
    }

    //==========================================================================
    private fun setupClientConnector(builder: WebClient.Builder, uri: String) = builder.also {

        require(uri.isNotEmpty()) { "webClient URI is empty (${it.javaClass.canonicalName})" }

        val httpClient = HttpClient.create(
            ConnectionProvider.builder("Default connection provider")
                .maxConnections(env.maxConnections)
                .maxIdleTime(env.maxIdleTime.seconds.toJavaDuration())
                .maxLifeTime(env.maxLifeTime.seconds.toJavaDuration())
                .pendingAcquireTimeout(env.pendingAcquireTimeout.seconds.toJavaDuration())
                .evictInBackground(env.evictInBackground.seconds.toJavaDuration())
                .build()
        )
            .also {
                if (uri.startsWith(URI_HTTPS)) {
                    it.secure { t: SslProvider.SslContextSpec ->
                        t.sslContext(SslContextBuilder
                            .forClient()
                            .trustManager(INSTANCE)
                            .build().also {
                                logger.debug("$uri: SslContext was created: ${it.javaClass.canonicalName} ")
                            })
                    }
                }
                logger.debug("$uri: HttpClient was created: ${it.javaClass.canonicalName} ")
            }

        it.clientConnector(ReactorClientHttpConnector(httpClient).also {
            logger.debug(
                "$uri: ReactorClientHttpConnector was created: ${it.javaClass.canonicalName} "
            )
        })
    }

    //===========================================================================
    fun <T> Mono<T>.monoMeasureTimeMillis(uriPath: String): Mono<T> = transform {
        val sw = LateInitVal<StopWatcher>()
        doOnSubscribe { sw.init(StopWatcher(javaClass.simpleName)) }
            .doOnSuccess {
                val uriInfo = "$serverUri$uriPath]::${sw.value.stringExecutionTime}"
                logger.debug { "webClient request [$uriInfo]" }
                logger.logRequestInternal(sw.value.executionTime, env.queryMaxTimeExec) { "[$uriInfo]" }
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // POST
    protected fun <P : Any, R> webClientExecute(
        httpRoute: RouteUrl,
        postBody: P,
        respClass: Class<R>,
        headersConsumer: Consumer<HttpHeaders> = EMPTY_HTTP_HEADERS,
    ) = webClientExecute(httpRoute, postBody, respClass, headersConsumer) {}

    protected fun <P : Any, R> webClientExecute(
        httpRoute: RouteUrl,
        postBody: P,
        respClass: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        onStatusProcessor: WebClientOnStatusProcessor = EMPTY_STATUS_PROCESSOR,
    ) = webClientExecute(httpRoute, postBody, respClass, { it }, headersConsumer, onStatusProcessor)

    protected fun <P : Any, R> webClientExecute(
        httpRoute: RouteUrl,
        postBody: P,
        respClass: Class<R>,
        uriBuilder: WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders> = EMPTY_HTTP_HEADERS,
        onStatusProcessor: WebClientOnStatusProcessor = EMPTY_STATUS_PROCESSOR,
    ): Mono<R> = (true.takeUnless { env.testConnection } ?: isHostPortAvailable()).let {
        require(!env.junitMode) { "Illegal webClient call in junitMode (${this.javaClass.canonicalName})" }
        require(it) { "$httpRoute: host/route is not available ($serverUri)" }
        doWebClientCall {
            webClient
                .post()
                .uri { uriBuilder(it.path(httpRoute)).build() }
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .headers(headersConsumer)
                .body(just(postBody), postBody.javaClass)
                .retrieve()
                .onStatus({ responseStatus -> responseStatus.isError },
                    {  logger.debug { "### responseStatus = $it" }
                        onStatusProcessor(it)
                        error(ResponseStatusException(it.statusCode()))
                    })
                .bodyToMono(respClass)
                .retry(ONE_ATTEMPT)
                .monoMeasureTimeMillis(httpRoute)
        }
    }

    protected fun <R> webClientExecuteMultipart(
        url: String,
        bodyInserter: BodyInserters.MultipartInserter,
        respClass: Class<R>,
        headersConsumer: Consumer<HttpHeaders> = EMPTY_HTTP_HEADERS,
        onStatusProcessor: WebClientOnStatusProcessor = EMPTY_STATUS_PROCESSOR,
    ): Mono<R> = (true.takeUnless { env.testConnection } ?: isHostPortAvailable()).let {
        require(!env.junitMode) { "Illegal webClient call in junitMode (${this.javaClass.canonicalName})" }
        require(it) { "$url: host/route is not available ($serverUri)" }

        doWebClientCall {
            webClient
                .post()
                .uri(url)
                .contentType(MULTIPART_FORM_DATA)
                .accept(APPLICATION_JSON)
                .headers(headersConsumer)
                .body(bodyInserter)
                .retrieve()
                .onStatus(
                    { responseStatus -> responseStatus.isError },
                    {
                        onStatusProcessor(it)
                        error(ResponseStatusException(it.statusCode()))
                    }
                )
                .bodyToMono(respClass)
                .retry(ONE_ATTEMPT)
                .monoMeasureTimeMillis(url)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // GET
    protected fun <R> webClientExecute(
        httpRoute: RouteUrl,
        respClass: Class<R>,
    ) = webClientExecute(httpRoute, respClass, { it }, EMPTY_HTTP_HEADERS)

    protected fun <R> webClientExecute(
        httpRoute: RouteUrl,
        respClass: Class<R>,
        uriBuilder: WebClientUriBuilder,
    ) = webClientExecute(httpRoute, respClass, uriBuilder, EMPTY_HTTP_HEADERS)

    protected fun <R> webClientExecute(
        httpRoute: RouteUrl,
        respClass: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
    ) = webClientExecute(httpRoute, respClass, { it }, headersConsumer)

    protected fun <R> webClientExecute(
        httpRoute: RouteUrl,
        respClass: Class<R>,
        uriBuilder: WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>,
    ): Mono<R> = (true.takeUnless { env.testConnection } ?: isHostPortAvailable()).let {
        require(!env.junitMode) { "Illegal webClient call in junitMode" }
        require(it) { "$httpRoute: host/route is not available ($serverUri)" }

        doWebClientCall {
            webClient
                .get()
                .uri { uriBuilder(it.path(httpRoute)).build() }
                .accept(APPLICATION_JSON)
                .headers(headersConsumer)
                .retrieve()
                .bodyToMono(respClass)
                .onErrorResume(WebClientResponseException::class.java) { responseException ->
                    empty<R>().takeIf { isClientError.test(responseException.statusCode) } ?: error(responseException)
                }
                .retry(ONE_ATTEMPT)
                .monoMeasureTimeMillis(httpRoute)
        }
    }

    private inline fun <R> doWebClientCall(webClientCall: WebClientCall<R>): Mono<R> =
        runCatching {
            webClientCall()
        }.getOrElse {
            // retry call
            logger.error { it }.run {
                if (isHostPortAvailable()) {
                    logger.warn { "retry call 2 ${serverUri.value} " }
                }
                webClientCall()
            }
        }

    private fun isHostPortAvailable(): Boolean = URI(serverUri.value).toURL().run {
        val actualPort = port.let { if (it > 0) it else 443 }
        val connectString = "$host:$actualPort"
        runCatching {
            Socket().use {
                logger.measureExecTime("$connectString: test connection", TIMEOUT_10000_MILLIS) {
                    it.connect(InetSocketAddress(host, actualPort), TIMEOUT_10000_MILLIS)
                    runCatching {
                        it.apply {
                            if (isConnected) {
                                it.close()
                            }
                        }
                    }.getOrElse {
                        logger.error { "$connectString: can't close connection ($it)" }
                    }
                }
            }
            true
        }.getOrElse {
            logger.error { "${connectString}: $it" }
            false
        }
    }
}
