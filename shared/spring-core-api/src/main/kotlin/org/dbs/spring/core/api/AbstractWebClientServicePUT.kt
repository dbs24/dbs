package org.dbs.spring.core.api

import org.dbs.consts.RestHttpConsts.ONE_ATTEMPT
import org.dbs.consts.RouteUrl
import org.dbs.consts.SpringCoreConst.EMPTY_HTTP_HEADERS
import org.dbs.consts.WebClientOnStatusProcessor
import org.dbs.consts.WebClientUriBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.core.publisher.Mono.just
import java.util.function.Consumer

object AbstractWebClientServicePUT {


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // WebClient With PUT method

    fun <P : Any, R> AbstractWebClientService.webClientExecutePUT(
        httpRoute: RouteUrl,
        putBody: P,
        respClass: Class<R>,
        onStatusProcessor: WebClientOnStatusProcessor = {}
    ) = webClientExecutePUT(httpRoute, putBody, respClass, EMPTY_HTTP_HEADERS, onStatusProcessor)

    fun <P : Any, R> AbstractWebClientService.webClientExecutePUT(
        httpRoute: RouteUrl,
        putBody: P,
        respClass: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        onStatusProcessor: WebClientOnStatusProcessor = {}

    ) = webClientExecutePUT(httpRoute, putBody, respClass, { it }, headersConsumer, onStatusProcessor)

    fun <P : Any, R> AbstractWebClientService.webClientExecutePUT(
        httpRoute: RouteUrl,
        putBody: P,
        respClass: Class<R>,
        uriBuilder: WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>,
        onStatusProcessor: WebClientOnStatusProcessor = {}
    ): Mono<R> = webClient
        .put()
        .uri { uriBuilder(it.path(httpRoute)).build() }
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .headers(headersConsumer)
        .body(just(putBody), putBody.javaClass)
        .retrieve()
        .onStatus({ responseStatus -> responseStatus.isError },
            {
                onStatusProcessor(it)
                error(ResponseStatusException(it.statusCode()))
            })
        .bodyToMono(respClass)
        .retry(ONE_ATTEMPT)
        .monoMeasureTimeMillis(httpRoute)

}
