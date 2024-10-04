package org.dbs.rest.api

import org.dbs.spring.core.api.EntityInfo
import reactor.core.publisher.Mono

fun interface MonoResponse<T : ResponseBody<E>, E : EntityInfo> {
    fun createMonoResponse(responseBody: T): Mono<T>
}
