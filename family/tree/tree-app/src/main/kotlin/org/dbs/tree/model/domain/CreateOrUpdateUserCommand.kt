package org.dbs.tree.model.domain

import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.consts.Password
import org.dbs.rest.api.nio.DomainCommand

data class CreateOrUpdateUserCommand(
    val oldLogin: EntityCode?,
    val login: EntityCode,
    val oldEmail: Email?,
    val email: Email,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val password: Password?
) : DomainCommand {

    val isNewUser: Boolean = oldLogin == null
    val isUpdateLogin: Boolean = !isNewUser && (oldLogin != login)
}

data class GetUserCredentialsCommand(
    val login: EntityCode,
) : DomainCommand