package org.dbs.analyst.value.solution

import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.analyst.service.SolutionService
import org.dbs.analyst.service.grpc.CmGrpcService
import org.dbs.analyst.service.grpc.GrpcGetFenSolution.getFunSolutionInternal
import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_DEPTH
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_FEN
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_TIMEOUT
import org.dbs.player.consts.PlayersConsts.CmQueryParams.QP_WHITE_MOVE
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.ServerRequestFuncs.qp
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.analyst.service.grpc.h1.convert.GetSolutionConverter as H1_CONV
import org.dbs.player.dto.solution.GetSolutionResponse as H1_RESP
import org.dbs.player.dto.solution.SolutionDto as H1_DTO
import org.dbs.player.dto.solution.SolutionDtoReq as H1_REQ

@JvmInline
value class GetSolutionRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactiveGetRequest<H1_DTO, H1_RESP> {

    override suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
    ) = serverRequest.run {
        processor.createResponseCo(this, H1_RESP::class.java) {
            solutionService.buildMonoResponse(this) {
                applyH1Converter(H1_CONV::class,
                    H1_REQ(
                        fen = qp(QP_FEN),
                        depth = qp(QP_DEPTH),
                        timeout = qp(QP_TIMEOUT)
                    ) , id())
                { with(cmGrpcService) { getFunSolutionInternal(it, ip()) } }
            }
        }
    }

    companion object {
        val solutionService by lazy { findService(SolutionService::class) }
        val cmGrpcService by lazy { findService(CmGrpcService::class) }
    }
}
