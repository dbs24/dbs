package org.dbs.rest.api

import org.dbs.spring.core.api.EntityInfo
import org.dbs.spring.core.api.PostRequestBody
import reactor.core.publisher.Mono

fun interface MonoResponse2<R : PostRequestBody, T : ResponseBody<E>, E : EntityInfo> {
    fun createMonoResponse(request: R, responseBody: T): Mono<T>
}
