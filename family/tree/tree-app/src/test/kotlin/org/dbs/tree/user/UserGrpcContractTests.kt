package org.dbs.tree.user

import io.kotest.common.KotestInternal
import org.dbs.consts.FieldError
import org.dbs.tree.BaseTreeGrpcTest
import org.dbs.tree.user.UserGrpcFuncs.buildUserCredentialsRequest
import org.dbs.tree.user.UserGrpcFuncs.buildUserPasswordRequest
import org.dbs.tree.user.UserGrpcFuncs.buildUserRequest
import org.dbs.tree.user.UserGrpcFuncs.buildUserStatusRequest
import org.dbs.tree.user.UserGrpcFuncs.createOrUpdateUserSuccess
import org.dbs.tree.user.UserGrpcFuncs.createOrUpdateUserWithValidationError
import org.dbs.tree.user.UserGrpcFuncs.getUserCredentials
import org.dbs.tree.user.UserGrpcFuncs.getUserCredentialsWithFails
import org.dbs.tree.user.UserGrpcFuncs.updateUserPasswordSuccess
import org.dbs.tree.user.UserGrpcFuncs.updateUserPasswordWithFail
import org.dbs.tree.user.UserGrpcFuncs.updateUserStatusSuccess
import org.dbs.tree.user.UserGrpcFuncs.updateUserStatusWithFail
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_CLOSED
import org.dbs.validator.Error.ALREADY_EXISTS
import org.dbs.validator.Error.MANDATORY_FIELD_MISSING
import org.dbs.validator.Field.SSS_USER_LOGIN

@OptIn(KotestInternal::class)
@Suppress("unused")
class UserGrpcContractTests : BaseTreeGrpcTest(), UserTestContract {

    override val userPrefix = "validgrpcuser"

    override suspend fun createUser(login: String, email: String, pass: String) {
        createOrUpdateUserSuccess(buildUserRequest(login, email, pass))
    }

    override suspend fun createUserExpectingDuplicate(login: String, email: String, pass: String) {
        createOrUpdateUserWithValidationError(
            buildUserRequest(login, email, pass),
            ALREADY_EXISTS to SSS_USER_LOGIN
        )
    }

    override suspend fun closeUser(login: String) {
        updateUserStatusSuccess(buildUserStatusRequest(login, ES_USER_CLOSED))
    }

    override suspend fun reopenUser(login: String) {
        updateUserStatusSuccess(buildUserStatusRequest(login, ES_USER_ACTUAL))
    }

    override suspend fun createUserWithEmptyFields() {
        createOrUpdateUserWithValidationError(
            buildUserRequest(login = "", email = "", password = "fp"),
            MANDATORY_FIELD_MISSING to SSS_USER_LOGIN
        )
    }

    override suspend fun createUserWithInvalidLogin(login: String, email: String, password: String, vararg errs: FieldError) {
        createOrUpdateUserWithValidationError(buildUserRequest(login = login, email = email, password = password), *errs)
    }

    override suspend fun createUserWithInvalidEmail(login: String, email: String, password: String, vararg errs: FieldError) {
        createOrUpdateUserWithValidationError(buildUserRequest(login = login, email = email, password = password), *errs)
    }

    override suspend fun createUserExpectingValidationError(
        vararg errs: FieldError,
        login: String, email: String, password: String?,
        phone: String?, firstName: String?, lastName: String?,
        middleName: String?, oldLogin: String?, oldEmail: String?,
    ) {
        createOrUpdateUserWithValidationError(
            buildUserRequest(
                login = login, email = email, password = password ?: "",
                phone = phone ?: "", firstName = firstName ?: "",
                lastName = lastName ?: "", middleName = middleName ?: "",
                oldLogin = oldLogin ?: "", oldEmail = oldEmail ?: ""
            ), *errs
        )
    }

    override suspend fun updateUser(login: String, email: String) {
        createOrUpdateUserSuccess(
            buildUserRequest(
                login = login, oldLogin = login,
                email = email, password = "Strong12Password", firstName = "firstName"
            )
        )
    }

    override suspend fun fetchUserCredentials(login: String) {
        getUserCredentials(buildUserCredentialsRequest(login))
    }

    override suspend fun fetchUserCredentialsExpectingNotFound(login: String, vararg errs: FieldError) {
        getUserCredentialsWithFails(buildUserCredentialsRequest(login), *errs)
    }

    override suspend fun fetchUserCredentialsWithInvalidLogin(login: String, vararg errs: FieldError) {
        getUserCredentialsWithFails(buildUserCredentialsRequest(login), *errs)
    }

    override suspend fun closeUserWithUnknownStatus(login: String, vararg errs: FieldError) {
        updateUserStatusWithFail(buildUserStatusRequest(login), *errs)
    }

    override suspend fun closeUserExpectingInvalidStatus(login: String, status: EntityStatus, vararg errs: FieldError) {
        updateUserStatusWithFail(buildUserStatusRequest(login, status), *errs)
    }

    override suspend fun updateUserPassword(login: String, oldPass: String, newPass: String) {
        updateUserPasswordSuccess(buildUserPasswordRequest(login, oldPass, newPass))
    }

    override suspend fun updateUserPasswordWithWrongOldPass(login: String, oldPass: String, newPass: String, vararg errs: FieldError) {
        updateUserPasswordWithFail(buildUserPasswordRequest(login, oldPass, newPass), *errs)
    }

    override suspend fun updateUserPasswordWithSamePass(login: String, pass: String, vararg errs: FieldError) {
        updateUserPasswordWithFail(buildUserPasswordRequest(login, pass, pass), *errs)
    }

    override suspend fun updateUserPasswordWithInvalidStatus(login: String, pass: String, vararg errs: FieldError) {
        updateUserPasswordWithFail(buildUserPasswordRequest(login, pass, pass), *errs)
    }

    init {
        include(userTestsFactory(this))
    }
}
