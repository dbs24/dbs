package org.dbs.rest.service

import org.dbs.consts.Arg2Mono
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_EUREKA_BALANCER_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_EUREKA_BALANCER_FIXED_POINTS_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_MAX_DURATION_OF_SEC
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_RETURN_ERROR_2_RESPONSE
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_EUREKA_BALANCER_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_EUREKA_BALANCER_FIXED_POINTS_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_MAX_DURATION_OF_SEC
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_RETURN_ERROR_2_RESPONSE
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.core.api.EntityInfo
import org.dbs.spring.core.api.RequestBody
import org.dbs.validator.Error.GENERAL_ERROR
import org.dbs.validator.Error.USER_ACCESS_DENIED
import org.dbs.validator.Field.GENERAL_FIELD
import org.dbs.validator.exception.EmptyBodyException
import org.dbs.application.core.exception.InternalAppException.Companion.getSuppressedErrMessage
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.RestHttpConsts
import org.dbs.consts.RestHttpConsts.ONE_ATTEMPT
import org.dbs.consts.SysConst.TEN_SECONDS
import org.dbs.rest.api.ResponseBody
import org.dbs.rest.api.enums.RestOperCode2HttpEnum.Companion.findActualHttpCode
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_ACCESS_DENIED_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_CONNECTION_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_EMPTY_RESPONSE_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_ERROR
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Duration.ofSeconds
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.reflect.KClass

@Deprecated("Obsolete processor",
    replaceWith = ReplaceWith("use new AbstractHttpRequestProcessor classes"))
abstract class AbstractMonoRestProcessor : AbstractApplicationService() {
    @Value("\${$CONFIG_RESTFUL_RETURN_ERROR_2_RESPONSE:$VALUE_RESTFUL_RETURN_ERROR_2_RESPONSE}")
    private val returnErrors2Response: Boolean = false

    @Value("\${$CONFIG_RESTFUL_MAX_DURATION_OF_SEC:$VALUE_RESTFUL_MAX_DURATION_OF_SEC}")
    val maxDurationOfSec: Long = 10L

    @Value("\${$CONFIG_EUREKA_BALANCER_ENABLED:$VALUE_EUREKA_BALANCER_ENABLED}")
    private val enableBalancer: Boolean = false

    @Value("\${$CONFIG_EUREKA_BALANCER_FIXED_POINTS_LIST:$VALUE_EUREKA_BALANCER_FIXED_POINTS_LIST}")
    private val fixedPointsList: Collection<String> = createCollection()
    private val isFixedPoint = Predicate { uri: String -> fixedPointsList.contains(uri) }

    protected inline fun <V : ResponseBody<E>, E : EntityInfo> createResponse(
        serverRequest: ServerRequest,
        classV: KClass<V>,
        crossinline funkResponse: org.dbs.consts.Arg2Mono<ServerRequest, V>
    ): Mono<ServerResponse> = createResponse(serverRequest, RequestBody::class, classV, funkResponse)

    protected inline fun <T : RequestBody, V : ResponseBody<E>, E : EntityInfo> createResponse(
        serverRequest: ServerRequest,
        classT: KClass<T>,
        classV: KClass<V>,
        crossinline funcResponse: org.dbs.consts.Arg2Mono<ServerRequest, V>
    ): Mono<ServerResponse> = serverRequest.run {

        val respBody =
            headers().firstHeader(USER_ACCESS_DENIED.name)?.run { createAccessDeniedError(classV, this) }
                ?: run {
                    funcResponse(serverRequest)
                }
                    .onErrorResume { createErrorAnswer(it, classV, serverRequest) }
                    .take(ofSeconds(maxDurationOfSec))
                    .switchIfEmpty { createEmptyBodyAnswer(classV, serverRequest) }
                    .doOnError(throwableConsumer)
                    .retry(ONE_ATTEMPT)
                    .log()
                    .cache(TEN_SECONDS)
        createServerResponse(respBody, classV)
    }

    fun <V : ResponseBody<E>, E : EntityInfo> createServerResponse(
        body: Mono<V>,
        classV: KClass<V>
    ): Mono<ServerResponse> =
        body.run {
            flatMap {
                ServerResponse
                    .status(findActualHttpCode(it.code, it.haveErrors))
                    .contentType(APPLICATION_JSON)
                    .body(body, classV.java)
                    .doOnError(throwableConsumer)
            }
        }

    val throwableConsumer by lazy {
        Consumer { throwable: Throwable ->
            log(throwable)
            { "Create response exception" }
        }
    }

    fun <V : ResponseBody<E>, E : EntityInfo> createAccessDeniedError(classV: KClass<V>, errMsg: String): Mono<V> =
        classV.java.getDeclaredConstructor().newInstance().also {
            it.apply {
                code = OC_ACCESS_DENIED_ERROR
                error = errMsg
                message = error
                logger.error(toString())
            }
        }.toMono()

    fun <V : ResponseBody<E>, E : EntityInfo> createEmptyBodyAnswer(
        classV: KClass<V>,
        serverRequest: ServerRequest
    ): Mono<V> =
        createErrorAnswer(
            OC_EMPTY_RESPONSE_ERROR,
            EmptyBodyException("Body is empty or request timeout error"),
            classV,
            serverRequest
        )

    fun <V : ResponseBody<E>, E : EntityInfo> createErrorAnswer(
        throwable: Throwable,
        classV: KClass<V>,
        serverRequest: ServerRequest
    ): Mono<V> = createErrorAnswer(OC_UNKNOWN_ERROR, throwable, classV, serverRequest)

    fun <V : ResponseBody<E>, E : EntityInfo> createErrorAnswer(
        restOperCodeEnum: RestOperCodeEnum,
        throwable: Throwable,
        classV: KClass<V>,
        serverRequest: ServerRequest
    ): Mono<V> = classV.java.getDeclaredConstructor().newInstance().also {
        it.apply {

            val suppressedErrMessage = getSuppressedErrMessage(throwable)
            val isConnectionError = suppressedErrMessage.contains(RestHttpConsts.Exceptions.EX_CONNECTION_RESET_BY_PEER)

            if (isConnectionError) {
                code = OC_CONNECTION_ERROR
                message = OC_CONNECTION_ERROR.getValue()
            } else {

                code = restOperCodeEnum
                error =
                    if (returnErrors2Response) throwable.javaClass.canonicalName + ": " +
                            throwable.message else OC_UNKNOWN_ERROR.getValue()
                message =
                    (if (returnErrors2Response) getSuppressedErrMessage(throwable) else OC_UNKNOWN_ERROR.getValue())
            }

            addErrorInfo(
                code,
                GENERAL_ERROR,
                GENERAL_FIELD,
                throwable.message ?: code.toString()
            )
            log(throwable) { "HTTP request: ${serverRequest.method().name()}: ${serverRequest.method().name()}" }
            complete()
        }
    }.toMono()

    fun isBalancedPoint(uri: String) = uri.run {
        logger.debug(
            "enableBalancer: $enableBalancer, fixedPointsList: $fixedPointsList, uri: $this, " +
                    "isFixed: $isFixedPoint.test(this) "
        )
        if (enableBalancer) !isFixedPoint.test(this) else false
    }
}
