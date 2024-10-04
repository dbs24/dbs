package org.dbs.auth.verify.clients.auth.dto

import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.Jwt
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class Jwt4VerifyDto(
    val jwt: Jwt,
) : RequestDto {
    override fun toString() = let { "Jwt4VerifyDto(jwt='${jwt.last15()}')" }
}

data class JwtVerifyResultDto(
    val verified: Boolean,
) : ResponseDto

data class VerifyJwtRequest(
    override val requestBodyDto: Jwt4VerifyDto
) : AbstractHttpRequestBody<Jwt4VerifyDto>()

data class VerifyJwtResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<JwtVerifyResultDto>(httpRequestId)

