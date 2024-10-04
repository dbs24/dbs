package org.dbs.customer.dto.customer

import org.dbs.consts.EntityCode
import org.dbs.consts.Password
import org.dbs.consts.SysConst
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class UpdateUserPasswordDto(
    val login: EntityCode,
    val newPassword: Password
) : RequestDto

data class CreatedUserPasswordDto(
    val login: EntityCode
) : ResponseDto

data class UpdateUserPasswordRequest(
    override val requestBodyDto: UpdateUserPasswordDto
) : AbstractHttpRequestBody<UpdateUserPasswordDto>()

data class UpdateUserPasswordResponse(
    private val httpRequestId: RequestId = SysConst.EMPTY_STRING
) : HttpResponseBody<CreatedUserPasswordDto>(httpRequestId)
