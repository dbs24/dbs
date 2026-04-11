package org.dbs.rest.service.value

import org.dbs.application.core.exception.InternalAppException.Companion.getSuppressedErrMessage
import org.dbs.consts.RestHttpConsts.Exceptions.EX_CONNECTION_RESET_BY_PEER
import org.dbs.consts.RestHttpConsts.ONE_ATTEMPT
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_MAX_DURATION_OF_SEC
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_RETURN_ERROR_2_RESPONSE
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_MAX_DURATION_OF_SEC
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_RETURN_ERROR_2_RESPONSE
import org.dbs.consts.SuspendNoArg2Mono
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.TEN_SECONDS
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.enums.RestOperCode2HttpEnum.Companion.findActualHttpCode
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_ACCESS_DENIED_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_CONNECTION_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_EMPTY_RESPONSE_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_ERROR
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.Dto
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.validator.Error.GENERAL_ERROR
import org.dbs.validator.Error.USER_ACCESS_DENIED
import org.dbs.validator.Field.GENERAL_FIELD
import org.dbs.validator.exception.EmptyBodyException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just
import java.time.Duration.ofSeconds
import java.util.function.Consumer
import kotlin.reflect.KClass

abstract class AbstractHttpRequestProcessor<S : ServerRequest> : AbstractApplicationService() {
    @Value("\${$CONFIG_RESTFUL_RETURN_ERROR_2_RESPONSE:$VALUE_RESTFUL_RETURN_ERROR_2_RESPONSE}")
    private val returnErrors2ResponseProp: Boolean = false

    @Value("\${$CONFIG_RESTFUL_MAX_DURATION_OF_SEC:$VALUE_RESTFUL_MAX_DURATION_OF_SEC}")
    private val maxDurationOfSecProp: Long = 10

    // Lazy initialization for class-level variables as requested
    private val returnErrors2Response by lazy { returnErrors2ResponseProp }
    val maxDurationOfSec by lazy { maxDurationOfSecProp }

    suspend inline fun <V : HttpResponseBody<E>, E : ResponseDto> createResponse(
        serverRequest: S,
        classV: KClass<V>,
        crossinline funkResponse: SuspendNoArg2Mono<V>
    ): ServerResponse = createResponse(serverRequest, EmptyHttpRequestBody::class, classV, funkResponse)

    suspend inline fun <RE : RequestDto, T : AbstractHttpRequestBody<RE>, V : HttpResponseBody<E>, E : ResponseDto>
            createResponse(
        serverRequest: S,
        classT: KClass<T>,
        classV: KClass<V>,
        crossinline funcResponse: SuspendNoArg2Mono<V>,
    ): ServerResponse = serverRequest.run {
        logger.debug { "createResponse[${classT.simpleName} => ${classV.simpleName}]" }

        val respBody: Mono<V> =
            headers().firstHeader(USER_ACCESS_DENIED.name)?.run { createAccessDeniedError(classV, this) }
                ?: runCatching { funcResponse() }.getOrElse { Mono.error(it) }
                    .onErrorResume { createErrorAnswer(it, classV, serverRequest) }
                    .timeout(ofSeconds(maxDurationOfSec))
                    .switchIfEmpty(createEmptyBodyAnswer(classV, serverRequest))
                    .doOnError(throwableConsumer)
                    .retry(ONE_ATTEMPT)
                    .log()
                    .cache(TEN_SECONDS)

        createServerResponse(respBody, classV)
    }

    fun <V : HttpResponseBody<E>, E : Dto> createServerResponse(
        body: Mono<V>,
        classV: KClass<V>,
    ): ServerResponse = body.flatMap { resp ->
        ServerResponse
            .status(findActualHttpCode(resp.responseCode, resp.errorsCount > 0))
            .contentType(resp.contentType)
            .body(body, classV.java)
            .doOnError(throwableConsumer)
    }.subscribeMono()

    val throwableConsumer by lazy {
        Consumer<Throwable> { throwable ->
            log(throwable) { "Create response exception" }
        }
    }

    fun <V : HttpResponseBody<E>, E : Dto> createAccessDeniedError(classV: KClass<V>, errMsg: String): Mono<V> =
        classV.java.getConstructor().newInstance().apply {
            responseCode = OC_ACCESS_DENIED_ERROR
            error = errMsg
            message = errMsg
            logger.error(toString())
        }.let { just(it) }

    fun <V : HttpResponseBody<E>, E : Dto> createEmptyBodyAnswer(
        classV: KClass<V>,
        serverRequest: S
    ): Mono<V> =
        createErrorAnswer(
            OC_EMPTY_RESPONSE_ERROR,
            EmptyBodyException("Body is empty or request timeout error"),
            classV,
            serverRequest
        )

    fun <V : HttpResponseBody<E>, E : Dto> createErrorAnswer(
        throwable: Throwable,
        classV: KClass<V>,
        serverRequest: S
    ): Mono<V> = createErrorAnswer(OC_UNKNOWN_ERROR, throwable, classV, serverRequest)

    private fun <V : HttpResponseBody<E>, E : Dto> createErrorAnswer(
        restOperCodeEnum: RestOperCodeEnum,
        throwable: Throwable,
        classV: KClass<V>,
        serverRequest: S
    ): Mono<V> = runCatching {
        classV.java.getConstructor(RequestId::class.java).newInstance(serverRequest.toString())
    }.getOrThrow().apply {
        val suppressedMsg = getSuppressedErrMessage(throwable)

        // Flat mapping logic using takeIf/elvis instead of if/else or direct null checks
        suppressedMsg.takeIf { it.contains(EX_CONNECTION_RESET_BY_PEER) }
            ?.let {
                responseCode = OC_CONNECTION_ERROR
                message = OC_CONNECTION_ERROR.getValue()
            } ?: run {
            responseCode = restOperCodeEnum
            error = returnErrors2Response.takeIf { it }
                ?.let { "${throwable::class.java.canonicalName}: ${throwable.message}" }
                ?: OC_UNKNOWN_ERROR.getValue()
            message = returnErrors2Response.takeIf { it }
                ?.let { suppressedMsg }
                ?: OC_UNKNOWN_ERROR.getValue()
        }

        addErrorInfo(responseCode, GENERAL_ERROR, GENERAL_FIELD, throwable.message ?: responseCode.toString())
        log(throwable) { "HTTP request: ${serverRequest.method().name()}: ${serverRequest.path()}" }
        complete()
    }.let { just(it) }

}

class EmptyHttpRequestBody : AbstractHttpRequestBody<RequestDto>(EMPTY_STRING) {
    override val requestBodyDto: RequestDto get() = TODO("Not implemented yet")
}
