package org.dbs.invite.dto.invite

import org.dbs.consts.EntityCode
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class UpdateInviteStatusDto(
    val inviteCode: EntityCode,
    val newStatus: String
) : RequestDto

data class CreatedInviteStatusDto(
    val inviteCode: String,
    val newStatus: String
) : ResponseDto

data class UpdateInviteStatusRequest(
    override val requestBodyDto: UpdateInviteStatusDto
) : AbstractHttpRequestBody<UpdateInviteStatusDto>()

data class UpdateInviteStatusResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedInviteStatusDto>(httpRequestId)
