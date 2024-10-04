package org.dbs.player.dto.solution

import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.player.consts.Fen
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class SolutionDtoReq(
    val fen: Fen,
    val depth: String,
    val timeout: String
) : RequestDto

data class SolutionDto(
    val fen: Fen,
    val status: String,
    val depth: Int,
    val timeout: Int
) : ResponseDto

data class GetSolutionResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<SolutionDto>(httpRequestId)
