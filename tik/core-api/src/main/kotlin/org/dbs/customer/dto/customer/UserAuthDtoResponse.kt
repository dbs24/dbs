package org.dbs.customer.dto.customer

import org.dbs.consts.EntityStatusName
import org.dbs.consts.Login
import org.dbs.consts.Password
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.ResponseDto


data class UserAuthDto(
    val userLogin: Login,
    val userPassword: Password,
    val userStatus: EntityStatusName
) : ResponseDto

data class GetUserCredentialsResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<UserAuthDto>(httpRequestId)
