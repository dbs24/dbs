package org.dbs.tree.user

import io.kotest.common.KotestInternal
import org.dbs.consts.FieldError
import org.dbs.tree.BaseTreeRestTest
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_CLOSED
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.dbs.user.dto.user.UpdateUserStatusDto
import org.dbs.validator.Error
import org.dbs.validator.Field

@OptIn(KotestInternal::class)
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

    override suspend fun createUserWithInvalidLogin(login: String, email: String, password: String, vararg errs: FieldError) {
        postQueryShouldFailWithValidationError("/createOrUpdate", createUserDto(login = login, email = email, password = password))
            .shouldContainErrors(*errs)
    }

    override suspend fun createUserWithInvalidEmail(login: String, email: String, password: String, vararg errs: FieldError) {
        postQueryShouldFailWithValidationError("/createOrUpdate", createUserDto(login = login, email = email, password = password))
            .shouldContainErrors(*errs)
    }

    override suspend fun createUserExpectingValidationError(
        vararg errs: FieldError,
        login: String, email: String, password: String?,
        phone: String?, firstName: String?, lastName: String?,
        middleName: String?, oldLogin: String?, oldEmail: String?,
    ) {
        postQueryShouldFailWithValidationError("/createOrUpdate",
            CreateOrUpdateUserDto(
                oldLogin = oldLogin, login = login,
                oldEmail = oldEmail, email = email,
                phone = phone, firstName = firstName,
                lastName = lastName, middleName = middleName,
                password = password
            )
        ).shouldContainErrors(*errs)
    }

    override suspend fun updateUser(login: String, email: String) {
        executePost<CreateOrUpdateUserDto, CreatedUserDto>(
            uri = "/createOrUpdate",
            requestBody = createUserDto(login = login, email = email).copy(oldLogin = login)
        ) { expectStatus().isOk }
    }

    override suspend fun fetchUserCredentials(login: String) {}

    override suspend fun fetchUserCredentialsExpectingNotFound(login: String, vararg errs: FieldError) {}

    override suspend fun fetchUserCredentialsWithInvalidLogin(login: String, vararg errs: FieldError) {}

    override suspend fun closeUserWithUnknownStatus(login: String, vararg errs: FieldError) {
        postQueryShouldFailWithValidationError("/updateStatus", UpdateUserStatusDto(login, "FAKED_STATUS"))
            .shouldContainErrors(*errs)
    }

    override suspend fun closeUserExpectingInvalidStatus(login: String, status: EntityStatus, vararg errs: FieldError) {
        postQueryShouldFailWithValidationError("/updateStatus", UpdateUserStatusDto(login, status.entityStatusName))
            .shouldContainErrors(*errs)
    }

    override suspend fun updateUserPassword(login: String, oldPass: String, newPass: String) {}

    override suspend fun updateUserPasswordWithWrongOldPass(login: String, oldPass: String, newPass: String, vararg errs: FieldError) {}

    override suspend fun updateUserPasswordWithSamePass(login: String, pass: String, vararg errs: FieldError) {}

    override suspend fun updateUserPasswordWithInvalidStatus(login: String, pass: String, vararg errs: FieldError) {}

    init {
        include(userTestsFactory(this))
    }
}
