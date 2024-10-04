package org.dbs.rest.service.value

import org.dbs.rest.api.consts.H1_PROCESSOR
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

interface GenericRequest<R : ServerRequest> {
    suspend fun buildResponse(processor: H1_PROCESSOR<R>): ServerResponse
}