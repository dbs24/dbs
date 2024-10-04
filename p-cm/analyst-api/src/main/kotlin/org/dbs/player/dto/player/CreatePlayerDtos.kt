package org.dbs.player.dto.player

import org.dbs.consts.*
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdatePlayerDto(
    val oldLogin: EntityCodeNull,
    val login: EntityCode,
    val oldEmail: String?,
    val email: String,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val password: PasswordNull,
) : RequestDto

data class CreatedPlayerDto(
    val modifiedLogin: String,
    val email: String
) : ResponseDto

data class CreateOrUpdatePlayerRequest(
    override val requestBodyDto: CreateOrUpdatePlayerDto
) : AbstractHttpRequestBody<CreateOrUpdatePlayerDto>()

data class CreatePlayerResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedPlayerDto>(httpRequestId)
