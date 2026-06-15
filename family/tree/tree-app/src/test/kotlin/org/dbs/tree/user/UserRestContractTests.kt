package org.dbs.tree.user

import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_CLOSED
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.dbs.user.dto.user.UpdateUserStatusDto
import org.dbs.validator.Error
import org.dbs.validator.Field

@Suppress("unused")
class UserRestContractTests : BaseTreeRestTest(), UserTestContract {

    override val userPrefix = "validrestuser"
    override val requestMapping = "/users"

    override suspend fun createUser(login: String, email: String, pass: String) {
        createUser(createUserDto(login, email, pass))
    }

    override suspend fun createUserExpectingDuplicate(login: String, email: String, pass: String) {
        postQueryShouldFailWithValidationError("/createOrUpdate", createUserDto(login, email, pass))
            .shouldContainErrors(Error.ALREADY_EXISTS to Field.SSS_USER_LOGIN)
    }

    override suspend fun closeUser(login: String) {
        updateUserStatus(UpdateUserStatusDto(login, ES_USER_CLOSED.entityStatusName))
    }

    override suspend fun reopenUser(login: String) {
        updateUserStatus(UpdateUserStatusDto(login, ES_USER_ACTUAL.entityStatusName))
    }

    override suspend fun createUserWithEmptyFields() {
        postQueryShouldFailWithValidationError("/createOrUpdate", createUserDto(login = "", email = "", password = "Strong12Password"))
    }

    override suspend fun createUserWithInvalidLogin(login: String, email: String, password: String, vararg errs: Pair<Error, Field>) {
        postQueryShouldFailWithValidationError("/createOrUpdate", createUserDto(login = login, email = email, password = password))
            .shouldContainErrors(*errs)
    }

    override suspend fun createUserWithInvalidEmail(login: String, email: String, password: String, vararg errs: Pair<Error, Field>) {
        postQueryShouldFailWithValidationError("/createOrUpdate", createUserDto(login = login, email = email, password = password))
            .shouldContainErrors(*errs)
    }

    override suspend fun updateUser(login: String, email: String) {
        executePost<CreateOrUpdateUserDto, CreatedUserDto>(
            uri = "/createOrUpdate",
            requestBody = createUserDto(login = login, email = email).copy(oldLogin = login)
        ) { expectStatus().isOk }
    }

    override suspend fun fetchUserCredentials(login: String) {}

    override suspend fun fetchUserCredentialsExpectingNotFound(login: String, vararg errs: Pair<Error, Field>) {}

    override suspend fun fetchUserCredentialsWithInvalidLogin(login: String, vararg errs: Pair<Error, Field>) {}

    override suspend fun closeUserWithUnknownStatus(login: String, status: String, vararg errs: Pair<Error, Field>) {
        postQueryShouldFailWithValidationError("/updateStatus", UpdateUserStatusDto(login, status))
            .shouldContainErrors(*errs)
    }

    override suspend fun closeUserExpectingInvalidStatus(login: String, status: String, vararg errs: Pair<Error, Field>) {
        postQueryShouldFailWithValidationError("/updateStatus", UpdateUserStatusDto(login, status))
            .shouldContainErrors(*errs)
    }

    override suspend fun updateUserPassword(login: String, oldPass: String, newPass: String) {}

    override suspend fun updateUserPasswordWithWrongOldPass(login: String, oldPass: String, newPass: String, vararg errs: Pair<Error, Field>) {}

    override suspend fun updateUserPasswordWithSamePass(login: String, pass: String, vararg errs: Pair<Error, Field>) {}

    override suspend fun updateUserPasswordWithInvalidStatus(login: String, pass: String, vararg errs: Pair<Error, Field>) {}

    init {
        include(userTestsFactory(this))
    }
}
