package org.dbs.goods.dto.user

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class UpdateUserStatusDto(
    val login: EntityCode,
    val newStatus: String
) : RequestDto

data class CreatedUserStatusDto(
    val login: String,
    val newStatus: String
) : ResponseDto

data class UpdateUserStatusRequest(
    override val requestBodyDto: UpdateUserStatusDto
) : AbstractHttpRequestBody<UpdateUserStatusDto>()

data class UpdateUserStatusResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedUserStatusDto>(httpRequestId)
