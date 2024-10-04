package org.dbs.test.ko

import org.dbs.consts.SpringCoreConst.EMPTY_HTTP_HEADERS
import org.dbs.consts.WebClientUriBuilder
import org.dbs.spring.core.api.EntityInfo
import org.dbs.spring.core.api.PostRequestBody
import org.dbs.consts.Arg2Generic
import org.dbs.consts.RouteUrl
import org.dbs.consts.SuspendArg2Generic
import org.dbs.rest.api.ResponseBody
import org.dbs.rest.api.enums.RestOperCodeEnum.Companion.badResponse
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_OK
import org.dbs.rest.api.nio.HttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import org.dbs.test.core.SysTestConsts.Postgres.failedMsgTemplate
import org.dbs.test.exception.EmptyCreatedEntityException
import org.dbs.test.exception.EmptyResponseBodyException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import reactor.core.publisher.Mono.just
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

object WebTestClientFuncs {

    //==================================================================================================================
    fun <T : ResponseBody<E>, E : EntityInfo> AbstractKoTestBehaviorSpec.checkTestResultV1(
        responseBody: T,
        string: String
    ) {
        responseBody.run {
            logger.info("Validate test results: $this")
            val sb = StringBuilder(255)
            sb.append(string)
            sb.append(", response code is: $code")
            message?.apply { sb.append(", message: " + message) }
            errors.apply {
                errors.forEach { error ->
                    sb.append(
                        """
         error: $error"""
                    )
                }
            }
            if (badResponse.test(code)) {
                logger.error("### - $code")
                logger.error("### - $error")
                logger.error("### - $errors")
            }
            require(code == OC_OK) { "Invalid response code ($code)" }
            require(errors.isEmpty()) { "Errors array should by empty: [$errors]" }
            require(createdEntity.hashCode() != 0) { "${responseBody.javaClass.canonicalName}: created entity is null" }

            //assertIsNull(String::class.java, responseBody.error, "error object")
        }
    }

    //==================================================================================================================
    fun <T : HttpResponseBody<E>, E : ResponseDto> AbstractKoTestBehaviorSpec.checkTestResultV2(
        responseBody: T,
        string: String
    ) {
        responseBody.run {
            logger.info("Validate test results: [${this.responseEntity}]")
            val sb = StringBuilder(255)
            sb.append(string)
            sb.append(", response code is: ${responseCode.toString()}")
            message.apply { sb.append(", message: " + message) }
            errors.apply {
                errors.forEach { error ->
                    sb.append(
                        """
         error: $error"""
                    )
                }
            }
            if (badResponse.test(responseCode)) {
                logger.error("### - $responseCode")
                logger.error("### - $error")
                logger.error("### - $errors")
            }
            require(responseCode == OC_OK) { "Invalid response code ($responseCode)" }
            require(errors.isEmpty()) { "Errors array should by empty: [$errors]" }
            require(responseEntity.hashCode() != 0) { "${responseBody.javaClass.canonicalName}: created entity is null" }

            //assertIsNull(String::class.java, responseBody.error, "error object")
        }
    }

    private inline fun <R> AbstractKoTestBehaviorSpec.findWebTestClient4GetEndpoint(
        testedRoute: RouteUrl,
        respClass: Class<R>,
        crossinline uriBuilder: WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>
    ): R = getExistsWebTestClient()
        .get()
        .uri { uriBuilder(it.path(testedRoute)).build() }
        .headers(headersConsumer)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk
        .expectBody(respClass)
        .returnResult()
        .responseBody
        .let {
            logger.info { "$testedRoute: $it" }
            it
        } ?: throw EmptyResponseBodyException(testedRoute)

    private fun <P : Any, R: Any> AbstractKoTestBehaviorSpec.findWebTestClient4PostEndpoint(
        testedRoute: RouteUrl,
        requestBody: P,
        respClass: KClass<R>,
        headersConsumer: Consumer<HttpHeaders>
    ): R = getExistsWebTestClient()
        .post()
        .uri { it.path(testedRoute).build() }
        .headers(headersConsumer)
        .accept(APPLICATION_JSON)
        .body(just(requestBody), requestBody.javaClass)
        .exchange()
        .expectStatus()
        .isOk
        .expectBody(respClass.java)
        .returnResult()
        .responseBody
        .let {
            logger.info { "$testedRoute: $it" }
            it
        } ?: throw EmptyResponseBodyException(testedRoute)


    //------------------------------------------------------------------------------------------------------------------
    fun <R : ResponseBody<E>, E : EntityInfo, T> AbstractKoTestBehaviorSpec.executeGetRequestV1(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: org.dbs.consts.WebClientUriBuilder,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV1(testedRoute, classResponse, uriBuilder, EMPTY_HTTP_HEADERS, func)

    fun <R : ResponseBody<E>, E : EntityInfo, T> AbstractKoTestBehaviorSpec.executeGetRequestV1(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV1(testedRoute, classResponse, { it }, headersConsumer, func)

    fun <R : ResponseBody<E>, E : EntityInfo, T> AbstractKoTestBehaviorSpec.executeGetRequestV1(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: org.dbs.consts.WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = findWebTestClient4GetEndpoint(testedRoute, classResponse, uriBuilder, headersConsumer)
        .let {
            checkTestResultV1(it, String.format(failedMsgTemplate, testedRoute.uppercase(Locale.getDefault())))
            it.createdEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)


    //------------------------------------------------------------------------------------------------------------------
    fun <R : HttpResponseBody<E>, E : ResponseDto, T> AbstractKoTestBehaviorSpec.executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: WebClientUriBuilder,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV2(testedRoute, classResponse, uriBuilder, EMPTY_HTTP_HEADERS, func)

    fun <R : HttpResponseBody<E>, E : ResponseDto, T> AbstractKoTestBehaviorSpec.executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV2(testedRoute, classResponse, { it }, headersConsumer, func)

    fun <R : HttpResponseBody<E>, E : ResponseDto, T> AbstractKoTestBehaviorSpec.executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = findWebTestClient4GetEndpoint(testedRoute, classResponse, uriBuilder, headersConsumer)
        .let {
            checkTestResultV2(it, String.format(failedMsgTemplate, testedRoute.uppercase(Locale.getDefault())))
            it.responseEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)
    //==================================================================================================================

    suspend fun <P : PostRequestBody, R : ResponseBody<E>, E : EntityInfo, T> AbstractKoTestBehaviorSpec.executePostRequestV1(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: KClass<R>,
        func: Arg2Generic<E, T>
    ) = executePostRequestV1(testedRoute, requestBody, classResponse, EMPTY_HTTP_HEADERS, func)

    suspend fun <P : PostRequestBody, R : ResponseBody<E>, E : EntityInfo, T>
            AbstractKoTestBehaviorSpec.executePostRequestV1(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: KClass<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: SuspendArg2Generic<E, T>
    ) = findWebTestClient4PostEndpoint(testedRoute, requestBody, classResponse, headersConsumer)
        .let {
            checkTestResultV1(it, String.format(failedMsgTemplate, testedRoute.uppercase(Locale.getDefault())))
            it.createdEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)

    //------------------------------------------------------------------------------------------------------------------
    suspend fun <D : RequestDto, P : HttpRequestBody<D>, R : HttpResponseBody<E>, E : ResponseDto, T>
            AbstractKoTestBehaviorSpec.executePostRequestV2(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: KClass<R>,
        func: Arg2Generic<E, T>
    ) = executePostRequestV2(testedRoute, requestBody, classResponse, EMPTY_HTTP_HEADERS, func)

    suspend fun <D : RequestDto, P : HttpRequestBody<D>, R : HttpResponseBody<E>, E : ResponseDto, T>
            AbstractKoTestBehaviorSpec.executePostRequestV2(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: KClass<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: SuspendArg2Generic<E, T>
    ) = findWebTestClient4PostEndpoint(testedRoute, requestBody, classResponse, headersConsumer)
        .let {
            checkTestResultV2(it, String.format(failedMsgTemplate, testedRoute.uppercase(Locale.getDefault())))
            it.responseEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)

}
