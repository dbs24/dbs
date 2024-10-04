package org.dbs.invite.dto.invite

import org.dbs.consts.EntityCode
import org.dbs.consts.EntityCodeNull
import org.dbs.consts.OperDateDto
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdateInviteDto(
    val inviteCode: EntityCodeNull,
    val playerLogin: EntityCode,
    val gameType: Int,
    val validDate: OperDateDto,
    val requiredRating: Int?,
    val whiteSide: Boolean?
) : RequestDto

data class CreatedInviteDto(
    val inviteCode: String,
    val playerLogin: EntityCode,
    val status: String
) : ResponseDto

data class CreateOrUpdateInviteRequest(
    override val requestBodyDto: CreateOrUpdateInviteDto
) : AbstractHttpRequestBody<CreateOrUpdateInviteDto>()

data class CreateInviteResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedInviteDto>(httpRequestId)
