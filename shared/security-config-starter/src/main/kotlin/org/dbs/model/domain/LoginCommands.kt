package org.dbs.model.domain

import org.dbs.consts.Password
import org.dbs.model.IssuedJwt
import org.dbs.model.RefreshJwt
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.utils.lateInitProperty

data class LoginUserCommand(
    val login: String,
    val password: Password,
    val userAgent: String
) : DomainCommand {
    override fun toString() = "{login: $login, password: **** }"
}

data class RefreshTokensCommand(
    val accessToken: String,
    val refreshToken: String,
    val userAgent: String
) : DomainCommand {
    var login: String by lateInitProperty()
    var issuedJwt: IssuedJwt by lateInitProperty()
    var refreshJwt: RefreshJwt by lateInitProperty()
}