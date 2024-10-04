package org.dbs.rest.dto.value

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.Jwt
import org.dbs.consts.Login
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class JwtList(
    val expiredJwt: Jwt,
    val refreshJwt: Jwt
) : RequestDto {
    override fun toString() = let {
        "JwtList(expiredJwt=${expiredJwt.last15()}, refreshJwt=${refreshJwt.last15()}"
    }
}

data class RefreshJwtRequest(
    @JsonAlias("entityInfo")
    @JsonProperty("requestBodyDto")
    override val requestBodyDto: JwtList
) : AbstractHttpRequestBody<JwtList>()

data class RefreshJwtResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<IssuedJwtResultDto>(httpRequestId)
