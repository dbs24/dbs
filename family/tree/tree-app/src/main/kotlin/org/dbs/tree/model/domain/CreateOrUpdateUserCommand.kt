package org.dbs.tree.model.domain

import org.dbs.application.value.Email
import org.dbs.application.value.UserLogin
import org.dbs.consts.Password
import org.dbs.rest.api.nio.DomainCommand

data class CreateOrUpdateUserCommand(
    val oldLogin: UserLogin?,
    val login: UserLogin,
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
    val login: UserLogin,
) : DomainCommand