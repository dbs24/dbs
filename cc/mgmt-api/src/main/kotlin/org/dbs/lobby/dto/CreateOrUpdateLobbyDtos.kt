package org.dbs.lobby.dto

import org.dbs.consts.EntityCode
import org.dbs.consts.EntityCodeNull
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdateLobbyDto(
    val oldLobbyCode: EntityCodeNull,
    val lobbyCode: EntityCode,
    val lobbyName: String?,
    val lobbyKind: String?,
) : RequestDto

data class CreatedLobbyDto(
    val lobbyCode: String,
    val status: String,
) : ResponseDto

data class CreateOrUpdateLobbyRequest(
    override val requestBodyDto: CreateOrUpdateLobbyDto
) : AbstractHttpRequestBody<CreateOrUpdateLobbyDto>()

data class CreateLobbyResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedLobbyDto>(httpRequestId)
