package org.dbs.model.domain

import org.dbs.consts.Password
import org.dbs.rest.api.nio.DomainCommand

data class LoginUserCommand(
    val login: String,
    val password: Password
) : DomainCommand {
    override fun toString() = "{login: $login, password: **** }"
}
