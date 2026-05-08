package org.dbs.user.dto.user

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import org.dbs.user.UserPassword

data class UpdateUserPasswordDto(
    val login: EntityCode,
    val newPassword: UserPassword
) : RequestDto

data class CreatedUserPasswordDto(
    val login: EntityCode
) : ResponseDto

data class UpdateUserPasswordRequest(
    override val requestBodyDto: UpdateUserPasswordDto
) : AbstractHttpRequestBody<UpdateUserPasswordDto>()

data class UpdateUserPasswordResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedUserPasswordDto>(httpRequestId)
