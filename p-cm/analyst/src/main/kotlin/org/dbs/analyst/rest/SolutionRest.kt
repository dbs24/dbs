package org.dbs.analyst.rest

import org.dbs.analyst.service.SolutionService
import org.dbs.analyst.value.solution.GetSolutionRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequest
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
class SolutionRest(
    private val solutionService: SolutionService,
) : H1_PROCESSOR<ServerRequest>() {
    suspend fun getSolution(serverRequest: ServerRequest) = serverRequest.doRequestCo {
        GetSolutionRequest(it).buildResponse(this@SolutionRest)
    }
}
