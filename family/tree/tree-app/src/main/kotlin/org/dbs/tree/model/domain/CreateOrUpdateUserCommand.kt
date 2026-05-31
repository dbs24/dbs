package org.dbs.tree.model.domain

import org.dbs.consts.Password
import org.dbs.ext.lateInitProperty
import org.dbs.rest.api.nio.DomainCommand
import org.dbs.tree.model.user.User

data class CreateOrUpdateUserCommand(
    val oldLogin: String?,
    val login: String,
    val oldEmail: String?,
    val email: String,
    val phone: String?,
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val password: Password?
) : DomainCommand {

    var updatedUser: User by lateInitProperty()
    val isNewUser: Boolean = oldLogin == null
    val isUpdateLogin: Boolean = !isNewUser && (oldLogin != login)
}

data class GetUserCredentialsCommand(
    val login: String,
) : DomainCommand {
    var updatedUser: User by lateInitProperty()
}

data class UpdateUserStatusCommand(
    val login: String,
    val status: String,
) : DomainCommand {
    var updatedUser: User by lateInitProperty()
}

data class UpdateUserPasswordCommand(
    val login: String,
    val oldPassword: String,
    val newPassword: String,
) : DomainCommand {
    var updatedUser: User by lateInitProperty()
}
