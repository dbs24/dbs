package org.dbs.rest.api.validator

import org.dbs.rest.service.ServerRequestFuncs.ip
import org.springframework.web.reactive.function.server.ServerRequest

object RestServiceFuncs {
    fun buildRemoteAddress(serverRequest: ServerRequest) = serverRequest.ip()

}
