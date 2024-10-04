package org.dbs.outOfService.dto

import org.dbs.consts.OperDateDto
import org.dbs.consts.OperDateDtoNull
import org.dbs.consts.StringNoteNull
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class GetCoreOutOfServiceDto(
    val updateDate: OperDateDtoNull,
    val startDate: OperDateDtoNull,
    val finishDate: OperDateDtoNull,
    val note: StringNoteNull,
) : ResponseDto

data class GetCoreOutOfServiceResponse (
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<GetCoreOutOfServiceDto>(httpRequestId)

data class GetCoreOutOfServiceRequest(
    val serviceName: String
) : RequestDto