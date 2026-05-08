package org.dbs.user.dto.user

import org.dbs.consts.EntityStatusName
import org.dbs.consts.Password
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.ResponseDto
import org.dbs.user.UserLogin


data class UserAuthDto(
    val userLogin: UserLogin,
    val userPassword: Password,
    val userStatus: EntityStatusName
) : ResponseDto

data class GetuserCredentialsResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<UserAuthDto>(httpRequestId)
