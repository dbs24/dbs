package org.dbs.rest.api


import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_REACTOR_REST_DEBUG
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_REACTOR_REST_DEBUG
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.consts.SysConst.VOID_CLASS
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just
import reactor.kotlin.core.publisher.toMono
import java.util.function.Consumer
import java.util.function.Predicate


abstract class ReactiveRestProcessor : AbstractApplicationService() {

    @Value("\${$CONFIG_REACTOR_REST_DEBUG:$VALUE_REACTOR_REST_DEBUG}")
    protected val restDebug: Boolean = false

    private val queryMetrics: QueryMetrics = QueryMetrics()

    //==========================================================================
    private val isVoidClass = Predicate { clazz: Class<*> -> clazz == VOID_CLASS }
    private val throwableConsumer = Consumer { throwable: Throwable ->
        logger.error("${throwable.cause}: ${throwable.message}")
    }

    @Suppress(UNCHECKED_CAST)
    private fun <V> getBody(serverRequest: ServerRequest, clazz: Class<V>): Mono<V> =
        if (isVoidClass.test(clazz)) EMPTY_STRING.toMono() as Mono<V> else serverRequest.bodyToMono(clazz)

    private fun <T, V> getEntity(entity: T, entityProcessor: EntityProcessor<T, V>): Mono<V> =
        just(entityProcessor.processEntity(if (entity == EMPTY_STRING) null else entity))

    private fun <T> getResponse(response: T): Mono<ServerResponse> = ServerResponse
        .status(OK)
        .contentType(APPLICATION_JSON)
        .bodyValue(response as Any)

    //==================================================================================================================
    protected fun <T, V> processServerRequest(
        serverRequest: ServerRequest,
        clazz: Class<T>,
        entityProcessor: EntityProcessor<T, V>
    ): Mono<ServerResponse> = queryMetrics.registryQuery(serverRequest.method().name(), serverRequest.path()).run {
        getBody(serverRequest, clazz)
            .flatMap { getEntity(it, entityProcessor) }
            .flatMap { getResponse(it) }
            .doOnError(throwableConsumer)
            .doFinally { queryMetrics.finishQuery(this, it.toString()) }
            .log()
    }

    //==========================================================================
    protected fun <T> log(mono: Mono<T>): Mono<T> = if (restDebug) mono.log() else mono

}
