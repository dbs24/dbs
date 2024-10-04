package org.dbs.outOfService.dto

import org.dbs.consts.OperDateDtoNull
import org.dbs.consts.StringNoteNull
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto


data class CreatedCoreOutOfServiceRespDto(
    val serviceDateStart: OperDateDtoNull,
    val serviceDateFinish: OperDateDtoNull,
) : ResponseDto

data class CreateOrUpdateCoreOutOfServiceDto(
    val serviceDateStart: OperDateDtoNull,
    val serviceDateFinish: OperDateDtoNull,
    val note: StringNoteNull,
) : RequestDto

data class CreateOrUpdateCoreOutOfServiceResponse (
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedCoreOutOfServiceRespDto>(httpRequestId)

data class CreateOrUpdateCoreOutOfServiceRequest(
    override val requestBodyDto: CreateOrUpdateCoreOutOfServiceDto
) : AbstractHttpRequestBody<CreateOrUpdateCoreOutOfServiceDto>()