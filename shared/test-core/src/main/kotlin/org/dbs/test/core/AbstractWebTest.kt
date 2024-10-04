package org.dbs.test.core

import org.dbs.consts.SpringCoreConst.EMPTY_HTTP_HEADERS
import org.dbs.consts.WebClientUriBuilder
import org.dbs.spring.core.api.EntityInfo
import org.dbs.spring.core.api.PostRequestBody
import org.dbs.consts.Arg2Generic
import org.dbs.consts.RestHttpConsts.ROUTE_URI_LIVENESS
import org.dbs.consts.RestHttpConsts.SHUTDOWN_REQUEST_CLASS
import org.dbs.consts.RestHttpConsts.URI_CAN_SHUTDOWN
import org.dbs.consts.RouteUrl
import org.dbs.rest.api.ResponseBody
import org.dbs.rest.api.ShutdownRequest
import org.dbs.rest.api.enums.RestOperCodeEnum.Companion.badResponse
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_OK
import org.dbs.rest.api.nio.HttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import org.dbs.test.core.SysTestConsts.Postgres.failedMsgTemplate
import org.dbs.test.exception.EmptyCreatedEntityException
import org.dbs.test.exception.EmptyResponseBodyException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono.just
import java.time.Duration.ofMillis
import java.util.Locale.getDefault
import java.util.function.Consumer
import kotlin.Int.Companion.MAX_VALUE
import kotlin.random.Random


@AutoConfigureWebTestClient
abstract class AbstractWebTest : AbstractJUnitTest() {
    //    @Autowired
    //    private WebClientMgmt webClientMgmt;
    private lateinit var webTestClient: WebTestClient

    fun getExistsWebTestClient(): WebTestClient = webTestClient

    @Autowired
    fun initWebTestClient(webTestClient: WebTestClient) {
        this.webTestClient = webTestClient
    }

    val random by lazy { Random }

    @Test
    @Order(1)
    @DisplayName("contextLoads")
    fun contextLoads() {
        // contextLoads
    }

    //==========================================================================
    @Test
    @Order(10)
    @DisplayName("api liveness")
    fun initializeMainService() {
        //======================================================================
        // действие тестирование сервиса
        getExistsWebTestClient()
            .get()
            .uri { it.path(ROUTE_URI_LIVENESS).build() }
            .accept(APPLICATION_JSON) //.body(createdRetailLoanContract, classRetailLoanContract)
            .exchange() //.onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Pizza5xxException()))
            // and use the dedicated DSL to test assertions against the response
            .expectStatus()
            .isOk
    }

    //==========================================================================
    @BeforeEach
    fun setUp() {
        Hooks.onOperatorDebug()
        webTestClient = getExistsWebTestClient()
            .mutate()
            .responseTimeout(ofMillis(timeoutDefault.toLong()))
            .build()
    }

    @AfterEach
    fun close() {
        Hooks.resetOnOperatorDebug()
    }

    @Order(MAX_VALUE)
    @Test
    @DisplayName("Finish Test")
    @Throws(InterruptedException::class)
    fun test4wait() {
        runTest {
            var isFinished = false
            while (!isFinished) {
                Thread.sleep(2000)
                val shutdownRequest: ShutdownRequest = getExistsWebTestClient()
                    .post()
                    .uri { it.path(URI_CAN_SHUTDOWN).build() }
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .exchange()
                    .expectStatus()
                    .isOk
                    .expectBody(SHUTDOWN_REQUEST_CLASS)
                    .returnResult()
                    .responseBody as ShutdownRequest
                //logger.debug(shutdownRequest)
                isFinished = shutdownRequest.canShutDown
            }
        }
        logger.info("#finisk all tests ${this.javaClass.canonicalName}")
    }

    //==================================================================================================================
    fun <T : ResponseBody<E>, E : EntityInfo> checkTestResultV1(responseBody: T, string: String) {
        responseBody.run {
            logger.info("Validate test results: $this")
            val sb = StringBuilder(255)
            sb.append(string)
            sb.append(", response code is: ${code.toString()}")
            message?.apply { sb.append(", message: " + message) }
            errors.apply {
                errors.forEach { error ->
                    errors.forEach { error ->
                        sb.append(
                            """
         error: $error"""
                        )
                    }
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
    fun <T : HttpResponseBody<E>, E : ResponseDto> checkTestResultV2(responseBody: T, string: String) {
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
            require(responseCode == OC_OK) { "Invalid response code ($responseCode), should be $OC_OK" }
            require(errors.isEmpty()) { "Errors array should by empty: [$errors]" }
            require(responseEntity.hashCode() != 0) { "${responseBody.javaClass.canonicalName}: created entity is null" }

            //assertIsNull(String::class.java, responseBody.error, "error object")
        }
    }

    private inline fun <R> findWebTestClient4GetEndpoint(
        testedRoute: RouteUrl,
        respClass: Class<R>,
        crossinline uriBuilder: org.dbs.consts.WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>
    ): R = getExistsWebTestClient()
        .get()
        .uri {
            uriBuilder(it.path(testedRoute)).build()
                .also { logger.debug { "$testedRoute: uri = $it" } }
        }
        .headers(headersConsumer)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk
        .expectBody(respClass)
        .returnResult()
        .responseBody
        .let { it.also { logger.info { "$testedRoute: $it" } } } ?: throw EmptyResponseBodyException(testedRoute)

    private fun <P : Any, R> findWebTestClient4PostEndpoint(
        testedRoute: RouteUrl,
        requestBody: P,
        respClass: Class<R>,
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
        .expectBody(respClass)
        .returnResult()
        .responseBody
        .let {
            logger.info { "$testedRoute: $it" }
            it
        } ?: throw EmptyResponseBodyException(testedRoute)


    //------------------------------------------------------------------------------------------------------------------
    fun <R : ResponseBody<E>, E : EntityInfo, T> executeGetRequestV1(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: org.dbs.consts.WebClientUriBuilder,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV1(testedRoute, classResponse, uriBuilder, EMPTY_HTTP_HEADERS, func)

    fun <R : ResponseBody<E>, E : EntityInfo, T> executeGetRequestV1(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV1(testedRoute, classResponse, { it }, headersConsumer, func)

    fun <R : ResponseBody<E>, E : EntityInfo, T> executeGetRequestV1(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: org.dbs.consts.WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = findWebTestClient4GetEndpoint(testedRoute, classResponse, uriBuilder, headersConsumer)
        .let {
            checkTestResultV1(it, String.format(failedMsgTemplate, testedRoute.uppercase(getDefault())))
            it.createdEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)


    //------------------------------------------------------------------------------------------------------------------
    fun <R : HttpResponseBody<E>, E : ResponseDto, T> executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV2(testedRoute, classResponse, { it }, EMPTY_HTTP_HEADERS, func)

    fun <R : HttpResponseBody<E>, E : ResponseDto, T> executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: org.dbs.consts.WebClientUriBuilder,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV2(testedRoute, classResponse, uriBuilder, EMPTY_HTTP_HEADERS, func)

    fun <R : HttpResponseBody<E>, E : ResponseDto, T> executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = executeGetRequestV2(testedRoute, classResponse, { it }, headersConsumer, func)

    fun <R : HttpResponseBody<E>, E : ResponseDto, T> executeGetRequestV2(
        testedRoute: RouteUrl,
        classResponse: Class<R>,
        uriBuilder: org.dbs.consts.WebClientUriBuilder,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = findWebTestClient4GetEndpoint(testedRoute, classResponse, uriBuilder, headersConsumer)
        .let {
            checkTestResultV2(it, String.format(failedMsgTemplate, testedRoute.uppercase(getDefault())))
            it.responseEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)
    //==================================================================================================================

    fun <P : PostRequestBody, R : ResponseBody<E>, E : EntityInfo, T> executePostRequestV1(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: Class<R>,
        func: Arg2Generic<E, T>
    ) = executePostRequestV1(testedRoute, requestBody, classResponse, EMPTY_HTTP_HEADERS, func)

    fun <P : PostRequestBody, R : ResponseBody<E>, E : EntityInfo, T> executePostRequestV1(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = findWebTestClient4PostEndpoint(testedRoute, requestBody, classResponse, headersConsumer)
        .let {
            checkTestResultV1(it, String.format(failedMsgTemplate, testedRoute.uppercase(getDefault())))
            it.createdEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)

    //------------------------------------------------------------------------------------------------------------------
    fun <D : RequestDto, P : HttpRequestBody<D>, R : HttpResponseBody<E>, E : ResponseDto, T> executePostRequestV2(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: Class<R>,
        func: Arg2Generic<E, T>
    ) =
        executePostRequestV2(testedRoute, requestBody, classResponse, EMPTY_HTTP_HEADERS, func)

    fun <D : RequestDto, P : HttpRequestBody<D>, R : HttpResponseBody<E>, E : ResponseDto, T> executePostRequestV2(
        testedRoute: RouteUrl,
        requestBody: P,
        classResponse: Class<R>,
        headersConsumer: Consumer<HttpHeaders>,
        func: Arg2Generic<E, T>
    ) = findWebTestClient4PostEndpoint(testedRoute, requestBody, classResponse, headersConsumer)
        .let {
            checkTestResultV2(it, String.format(failedMsgTemplate, testedRoute.uppercase(getDefault())))
            it.responseEntity?.let { ce -> func(ce) }
        } ?: throw EmptyCreatedEntityException(testedRoute)

}
