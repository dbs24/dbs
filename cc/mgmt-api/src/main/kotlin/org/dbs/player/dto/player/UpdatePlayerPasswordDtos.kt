package org.dbs.player.dto.player

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.player.PlayerPassword
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class UpdatePlayerPasswordDto(
    val login: EntityCode,
    val newPassword: PlayerPassword
) : RequestDto

data class CreatedPlayerPasswordDto(
    val login: EntityCode
) : ResponseDto

data class UpdatePlayerPasswordRequest(
    override val requestBodyDto: UpdatePlayerPasswordDto
) : AbstractHttpRequestBody<UpdatePlayerPasswordDto>()

data class UpdatePlayerPasswordResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedPlayerPasswordDto>(httpRequestId)
