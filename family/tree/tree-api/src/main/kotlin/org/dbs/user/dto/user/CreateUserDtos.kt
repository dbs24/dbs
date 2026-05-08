package org.dbs.user.dto.user

import org.dbs.consts.Email
import org.dbs.consts.EmailNull
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityCodeNull
import org.dbs.consts.PasswordNull
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdateUserDto(
    val oldLogin: EntityCodeNull,
    val login: EntityCode,
    val oldEmail: EmailNull,
    val email: Email,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val password: PasswordNull,
) : RequestDto

data class CreatedUserDto(
    val modifiedLogin: String,
    val email: String,
    val status: String
) : ResponseDto

data class CreateOrUpdateUserRequest(
    override val requestBodyDto: CreateOrUpdateUserDto
) : AbstractHttpRequestBody<CreateOrUpdateUserDto>()

data class CreateUserResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedUserDto>(httpRequestId)
