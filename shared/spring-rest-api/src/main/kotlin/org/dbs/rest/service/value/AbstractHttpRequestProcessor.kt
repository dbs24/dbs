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
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Duration.ofSeconds
import java.util.function.Consumer
import kotlin.reflect.KClass

abstract class AbstractHttpRequestProcessor<S : ServerRequest> : AbstractApplicationService() {
    @Value("\${$CONFIG_RESTFUL_RETURN_ERROR_2_RESPONSE:$VALUE_RESTFUL_RETURN_ERROR_2_RESPONSE}")
    private val returnErrors2Response: Boolean = false

    @Value("\${$CONFIG_RESTFUL_MAX_DURATION_OF_SEC:$VALUE_RESTFUL_MAX_DURATION_OF_SEC}")
    val maxDurationOfSec: Long = 10


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
                ?: run { funcResponse() }
                    .onErrorResume { createErrorAnswer(it, classV, serverRequest) }
                    .take(ofSeconds(maxDurationOfSec))
                    .switchIfEmpty { createEmptyBodyAnswer(classV, serverRequest) }
                    .doOnError(throwableConsumer)
                    .retry(ONE_ATTEMPT)
                    .log()
                    .cache(TEN_SECONDS)

        createServerResponse(respBody, classV)
    }

    fun <V : HttpResponseBody<E>, E : Dto> createServerResponse(
        body: Mono<V>,
        classV: KClass<V>,
    ): ServerResponse = body.run {
        flatMap {
            ServerResponse
                .status(findActualHttpCode(it.responseCode, it.errorsCount > 0))
                .contentType(it.contentType)
                .body(body, classV.java)
                .doOnError(throwableConsumer)
        }.subscribeMono()
    }

    val throwableConsumer by lazy {
        Consumer { throwable: Throwable ->
            log(throwable)
            { "Create response exception" }
        }
    }

    fun <V : HttpResponseBody<E>, E : Dto> createAccessDeniedError(classV: KClass<V>, errMsg: String): Mono<V> =
        classV.java.getDeclaredConstructor().newInstance().also {
            it.apply {
                responseCode = OC_ACCESS_DENIED_ERROR
                error = errMsg
                message = errMsg
                logger.error(toString())
            }
        }.toMono()

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
    ): Mono<V> = classV.java.getDeclaredConstructor(RequestId::class.java)
        .newInstance(serverRequest.toString()).apply {

            val suppressedErrMessage = getSuppressedErrMessage(throwable)
            val isConnectionError = suppressedErrMessage.contains(EX_CONNECTION_RESET_BY_PEER)

            if (isConnectionError) {
                responseCode = OC_CONNECTION_ERROR
                message = OC_CONNECTION_ERROR.getValue()
            } else {
                responseCode = restOperCodeEnum
                error =
                    if (returnErrors2Response) throwable.javaClass.canonicalName +
                            ": " + throwable.message else OC_UNKNOWN_ERROR.getValue()
                message =
                    (if (returnErrors2Response) suppressedErrMessage else OC_UNKNOWN_ERROR.getValue())
            }

            addErrorInfo(
                responseCode,
                GENERAL_ERROR,
                GENERAL_FIELD,
                throwable.message ?: responseCode.toString()
            )
            log(throwable) { "HTTP request: ${serverRequest.method().name()}: ${serverRequest.method().name()}" }
            complete()
        }.toMono()
}

class EmptyHttpRequestBody : AbstractHttpRequestBody<RequestDto>(EMPTY_STRING) {
    override val requestBodyDto: RequestDto
        get() = TODO("Not implemented yet")
}
