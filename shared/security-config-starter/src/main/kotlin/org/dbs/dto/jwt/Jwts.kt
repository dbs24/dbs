
package org.dbs.dto.jwt

import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class LoginUserDto(
    val login: String,
    val password: String,
) : RequestDto

data class LoginUserResponseDto(
    val accessToken: String,
    val accessValidUntil: Long,
    val refreshToken: String,
    val refreshValidUntil: Long,
) : ResponseDto


data class RefreshTokensDto(
    val accessToken: String,
    val refreshToken: String,
) : RequestDto

