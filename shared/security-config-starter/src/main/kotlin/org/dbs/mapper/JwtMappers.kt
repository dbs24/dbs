package org.dbs.mapper

import org.dbs.dto.jwt.LoginUserDto
import org.dbs.dto.jwt.RefreshTokensDto
import org.dbs.model.domain.LoginUserCommand
import org.dbs.model.domain.RefreshTokensCommand

object JwtMappers {

    fun LoginUserDto.toCommand(): LoginUserCommand =
        LoginUserCommand(login, password)

    fun RefreshTokensDto.toCommand(): RefreshTokensCommand =
        RefreshTokensCommand(accessToken, refreshToken)

}