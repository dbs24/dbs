package org.dbs.player.dto.player

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class UpdatePlayerStatusDto(
    val login: EntityCode,
    val newStatus: String
) : RequestDto

data class CreatedPlayerStatusDto(
    val login: String,
    val newStatus: String
) : ResponseDto

data class UpdatePlayerStatusRequest(
    override val requestBodyDto: UpdatePlayerStatusDto
) : AbstractHttpRequestBody<UpdatePlayerStatusDto>()

data class UpdatePlayerStatusResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedPlayerStatusDto>(httpRequestId)
