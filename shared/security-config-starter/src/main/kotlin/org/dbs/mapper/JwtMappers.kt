package org.dbs.mapper

import org.dbs.dto.jwt.LoginUserDto
import org.dbs.dto.jwt.RefreshTokensDto
import org.dbs.model.domain.LoginUserCommand
import org.dbs.model.domain.RefreshTokensCommand

object JwtMappers {

    fun LoginUserDto.toCommand(userAgent: String): LoginUserCommand =
        LoginUserCommand(login, password, userAgent)

    fun RefreshTokensDto.toCommand(userAgent: String): RefreshTokensCommand =
        RefreshTokensCommand(accessToken, refreshToken, userAgent)

}