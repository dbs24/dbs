package org.dbs.user.dto.user

import org.dbs.consts.Email
import org.dbs.consts.EmailNull
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityCodeNull
import org.dbs.consts.PasswordNull
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdateUserDto(
    val oldLogin: EntityCodeNull = null,
    val login: EntityCode,
    val oldEmail: EmailNull = null,
    val email: Email,
    val phone: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val password: PasswordNull = null,
) : RequestDto

data class CreatedUserDto(
    val modifiedLogin: String,
    val email: String?,
    val status: String
) : ResponseDto


data class UpdateUserStatusDto(
    val login: String,
    val status: String,
) : RequestDto

data class UpdatedUserDto(
    val modifiedLogin: String,
    val newStatus: String
) : ResponseDto
