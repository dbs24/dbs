package org.dbs.rest.dto.value

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.Jwt
import org.dbs.consts.Login
import org.dbs.consts.Password
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.SECURED
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class LoginUserDto(
    val login: Login,
    val password: Password
) : RequestDto {
    override fun toString() = let { "LoginUserDto(login='$login', pass='$SECURED')" }
}

data class IssuedJwtResultDto(
    val accessJwt: Jwt,
    val accessJwtExP: Long,
    val refreshJwt: Jwt,
    val refreshJwtExP: Long,
) : ResponseDto {
    override fun toString() = let {
        "IssuedJwts(accessJwt=${accessJwt.last15()}, accessJwtExP=$accessJwtExP, refreshJwt=${refreshJwt.last15()}, refreshJwtExP=$accessJwtExP)"
    }
}

data class LoginUserRequest(
    override val requestBodyDto: LoginUserDto,
) : AbstractHttpRequestBody<LoginUserDto>()

data class LoginUserResponse(
    private val httpRequestId: RequestId = EMPTY_STRING // TODO should requestId be empty?
) : HttpResponseBody<IssuedJwtResultDto>(httpRequestId)
