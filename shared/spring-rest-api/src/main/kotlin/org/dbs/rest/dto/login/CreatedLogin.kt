package org.dbs.rest.dto.login

import org.dbs.spring.core.api.EntityInfo
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.Jwt

data class CreatedLogin(
    val login: String,
    val jwt: Jwt,
    val refreshJwt: Jwt
) : EntityInfo {
    override fun toString() =
        "CreatedLogin(login=$login, jwt=${jwt.last15()}, refreshJwt=${refreshJwt.last15()}})"
}
