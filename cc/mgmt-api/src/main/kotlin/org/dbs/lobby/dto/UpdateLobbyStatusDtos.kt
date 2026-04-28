package org.dbs.lobby.dto

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class UpdateLobbyStatusDto(
    val lobbyCode: EntityCode,
    val newStatus: String,
) : RequestDto

data class CreatedLobbyStatusDto(
    val lobbyCode: String,
    val newStatus: String,
) : ResponseDto

data class UpdateLobbyStatusRequest(
    override val requestBodyDto: UpdateLobbyStatusDto
) : AbstractHttpRequestBody<UpdateLobbyStatusDto>()

data class UpdateLobbyStatusResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedLobbyStatusDto>(httpRequestId)
