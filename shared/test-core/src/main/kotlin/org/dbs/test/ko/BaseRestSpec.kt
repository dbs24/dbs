package org.dbs.test.ko

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SpringCoreConst.WebClientConsts.UNLIMITED_BUFFER
import org.dbs.rest.api.exception.ValidationErrorResponse
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import java.time.Duration.ofMillis

@AutoConfigureWebTestClient
abstract class BaseRestSpec : BaseSpec(), Logging {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Value("\${reactive.rest.timeout:200000}")
    protected val timeoutDefault: Int = 200000

    override val source = "RESTful"
    abstract val requestMapping: String

    init {
        beforeSpec {

        }

        afterSpec {

        }

        beforeTest {
            //clearDatabase()
        }
    }

    val getExistsWebTestClient: WebTestClient
        get () = webTestClient.run {
        Hooks.onOperatorDebug()
        mutate()
            .codecs { it.defaultCodecs().maxInMemorySize(UNLIMITED_BUFFER) }
            .responseTimeout(ofMillis(timeoutDefault.toLong()))
            .build()
    }

    inline fun <reified T : RequestDto, reified R : Any> executePost(
        defMapping: String = requestMapping,
        uri: String,
        requestBody: T,
        statusCheck: WebTestClient.ResponseSpec.() -> WebTestClient.ResponseSpec
    ): R = getExistsWebTestClient
            .post()
            .uri(defMapping + uri)
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(Mono.just(requestBody), T::class.java)
            .exchange()
            .let(statusCheck) // Применяем проверку статуса (.isOk или .is4xxClientError)
            .expectBody<R>()
            .returnResult()
            .responseBody ?: error("Body is missing (${R::class.simpleName})")

    inline fun <reified T : RequestDto, reified V : ResponseDto> postQuery(
        uri: String,
        requestBody: T,
        successAction: (V) -> Unit,
    ) {
        val response = executePost<T, V>(requestMapping, uri, requestBody) { expectStatus().isOk }
        successAction(response)
    }

    inline fun <reified T : RequestDto> postQueryShouldFailWithValidationError(
        uri: String,
        requestBody: T,
    ): ErrorBox {
        val response = executePost<T, ValidationErrorResponse>(requestMapping, uri, requestBody) {
            expectStatus().is4xxClientError
        }
        return ErrorBox(response.errors)
    }

    inline fun <reified T : RequestDto> postQueryShouldFailWithInternalError(
        uri: String,
        requestBody: T,
    ) {
        executePost<T, ValidationErrorResponse>(requestMapping, uri, requestBody) {
            expectStatus().is5xxServerError
        }

    }
}
