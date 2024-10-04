package org.dbs.outOfService.rest

import org.dbs.outOfService.service.CoreOutOfServiceService
import org.dbs.outOfService.value.CreateOrUpdateCoreOutOfServiceValueRequest
import org.dbs.outOfService.value.GetCoreOutOfServiceValueRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
class CoreOutOfServiceRest: H1_PROCESSOR<ServerRequest>() {
    suspend fun createOrUpdateCoreOutOfService(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        CreateOrUpdateCoreOutOfServiceValueRequest(it).buildResponse(
            this@CoreOutOfServiceRest
        )
    }

    suspend fun getCoreOutOfService(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        GetCoreOutOfServiceValueRequest(it).buildResponse(
            this@CoreOutOfServiceRest,
        )
    }
}
