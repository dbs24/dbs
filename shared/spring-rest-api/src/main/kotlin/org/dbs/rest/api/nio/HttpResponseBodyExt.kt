package org.dbs.rest.api.nio

import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.toMono

object HttpResponseBodyExt {
    fun <T : ResponseDto, H : HttpResponseBody<T>> Int.noEmpty(responseBody: H): Mono<H> =
        if (this > 0) responseBody.toMono() else empty()

}
