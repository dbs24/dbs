package org.dbs.mapper

import org.dbs.dto.jwt.LoginUserDto
import org.dbs.model.domain.LoginUserCommand

object JwtMappers {

    fun LoginUserDto.toCommand(): LoginUserCommand =
        LoginUserCommand(login, password)

}