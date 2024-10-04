package org.dbs.rest.api

import org.dbs.spring.core.api.EntityInfo
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

@Deprecated("Use value classes")
interface StreamProcessor<T : ResponseBody<E>, E : EntityInfo> : Logging {

    fun getMonoBody(): Mono<T>

    fun getSpResponseBody(): T

    fun getRemoteAddress(): String

    fun getServerRequest(): ServerRequest

    @Deprecated("Use newest assignResponse ")
    fun assignResponse(src: ResponseBody<*>, dst: T) {
        dst.code = src.code
        dst.message = src.message
        dst.error = src.error
        dst.errors.addAll(src.errors)
    }

    fun containErrors() = (getSpResponseBody().containErrors())
    fun assignBadParentResponse(src: ResponseBody<*>) = getSpResponseBody().apply {
        code = src.code
        src.message?.let { message = it }
        src.error?.let { error = it }
        src.errors.let { errors.addAll(it) }
    }
}
