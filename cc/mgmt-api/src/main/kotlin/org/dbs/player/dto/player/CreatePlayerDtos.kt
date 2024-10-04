package org.dbs.player.dto.player

import org.dbs.consts.AnyCode
import org.dbs.consts.BirthDateDto
import org.dbs.consts.CountryIsoCode
import org.dbs.consts.Email
import org.dbs.consts.EmailNull
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityCodeNull
import org.dbs.consts.PasswordNull
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.UriPath
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdatePlayerDto(
    val oldLogin: EntityCodeNull,
    val login: EntityCode,
    val oldEmail: EmailNull,
    val email: Email,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val password: PasswordNull,
    val birthDate: BirthDateDto?,
    val country: CountryIsoCode?,
    val gender: AnyCode?,
    val avatar: UriPath?,
    val smallAvatar: UriPath?,
) : RequestDto

data class CreatedPlayerDto(
    val modifiedLogin: String,
    val email: String,
    val status: String
) : ResponseDto

data class CreateOrUpdatePlayerRequest(
    override val requestBodyDto: CreateOrUpdatePlayerDto
) : AbstractHttpRequestBody<CreateOrUpdatePlayerDto>()

data class CreatePlayerResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedPlayerDto>(httpRequestId)
