package org.dbs.tree.user

import io.kotest.common.KotestInternal
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.stringSpec
import org.dbs.consts.FieldError
import org.dbs.test.ext.generateDefValidationTestsWithFail
import org.dbs.test.ko.BaseSpec.Companion.TEST_MAIL_DOMAIN
import org.dbs.test.ko.BaseSpec.Companion.testNum
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.validator.strategy.UserValidationPattern
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_CLOSED
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Isolate
interface UserTestContract: UserValidationPattern {
    val userPrefix: String
    val source: String

    suspend fun createUser(login: String, email: String, pass: String)
    suspend fun createUserExpectingDuplicate(login: String, email: String, pass: String)
    suspend fun createUserWithEmptyFields()
    suspend fun createUserWithInvalidLogin(login: String, email: String, password: String, vararg errs: FieldError)
    suspend fun createUserWithInvalidEmail(login: String, email: String, password: String, vararg errs: FieldError)

    suspend fun createUserExpectingValidationError(
        vararg errs: FieldError,
        login: String = "validuser",
        email: String = "valid@test.com",
        password: String? = "Strong12Password",
        phone: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        middleName: String? = null,
        oldLogin: String? = null,
        oldEmail: String? = null,
    )

    suspend fun updateUser(login: String, email: String)

    suspend fun fetchUserCredentials(login: String)
    suspend fun fetchUserCredentialsExpectingNotFound(login: String, vararg errs: FieldError)
    suspend fun fetchUserCredentialsWithInvalidLogin(login: String, vararg errs: FieldError)

    suspend fun closeUser(login: String)
    suspend fun closeUserWithUnknownStatus(login: String, vararg errs: FieldError)
    suspend fun closeUserExpectingInvalidStatus(login: String, status: EntityStatus, vararg errs: FieldError)
    suspend fun reopenUser(login: String)

    suspend fun updateUserPassword(login: String, oldPass: String, newPass: String)
    suspend fun updateUserPasswordWithWrongOldPass(login: String, oldPass: String, newPass: String, vararg errs: FieldError)
    suspend fun updateUserPasswordWithSamePass(login: String, pass: String, vararg errs: FieldError)
    suspend fun updateUserPasswordWithInvalidStatus(login: String, pass: String, vararg errs: FieldError)

    fun buildUserLogin() = "$userPrefix${testNum}"
}

@OptIn(KotestInternal::class)
fun userTestsFactory(contract: UserTestContract) = stringSpec {

    val src = contract.source

    generateDefValidationTestsWithFail("Try to create user via $src",
        UserTestContract::createUserExpectingValidationError, contract)

    "Create user via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong1Password")
    }

    "Create another user via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong12Password")
    }

    "Try to create existing user via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong1Password")
        contract.createUserExpectingDuplicate(login, "$login$TEST_MAIL_DOMAIN", "Strong1Password")
    }

    "Try to create invalid user via $src" {
        contract.createUserWithEmptyFields()
    }

    "Try to create user with invalid login via $src" {
        contract.createUserWithInvalidLogin(
            "vali", "valid_user2$TEST_MAIL_DOMAIN", "Strong12Password",
            Error.INVALID_ATTR_PATTERN_MISMATCH to Field.SSS_USER_LOGIN
        )
    }

    "Try to create user with invalid email via $src" {
        val login = contract.buildUserLogin()
        contract.createUserWithInvalidEmail(login, "invalid_mail", "Strong12Password",
            Error.INVALID_ATTR_PATTERN_MISMATCH to Field.SSS_USER_EMAIL)
    }

    "Update user via $src" {
        val login = contract.buildUserLogin()
        val email = "$login$TEST_MAIL_DOMAIN"
        contract.createUser(login, email, "Strong12Password")
        contract.updateUser(login, email)
    }

    "Get user credentials via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong12Password")
        contract.reopenUser(login)
        contract.fetchUserCredentials(login)
    }

    "Get user credentials with fail via $src" {
        contract.fetchUserCredentialsExpectingNotFound(
            "invalidgrpcuser0",
            Error.USER_DOES_NOT_EXISTS to Field.SSS_USER_LOGIN
        )
    }

    "Get user credentials with invalid login via $src" {
        contract.fetchUserCredentialsWithInvalidLogin(
            "loginNotExists#1",
            Error.INVALID_ATTR_PATTERN_MISMATCH to Field.SSS_USER_LOGIN
        )
    }

    "Close user via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong1Password")
        contract.closeUser(login)
    }

    "Try to close user with unknown status via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong12Password")
        contract.closeUserWithUnknownStatus(login, Error.UNKNOWN_ENTITY_STATUS to Field.SSS_USER_STATUS)
    }

    "Try to close user with invalid status via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong12Password")
        contract.closeUser(login)
        contract.closeUserExpectingInvalidStatus(login, ES_USER_CLOSED, Error.INVALID_ENTITY_STATUS to Field.SSS_USER_STATUS)
    }

    "Reopen user via $src" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", "Strong1Password")
        contract.closeUser(login)
        contract.reopenUser(login)
    }

    "Update user password via $src" {
        val login = contract.buildUserLogin()
        val pass = "Strong12Password"
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", pass)
        contract.reopenUser(login)
        contract.updateUserPassword(login, pass, "Strong22Password")
    }

    "Try to update user with invalid password via $src" {
        val login = contract.buildUserLogin()
        val pass = "Strong12Password"
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", pass)
        contract.updateUserPasswordWithWrongOldPass(
            login, "${pass}1", "Strong2Password",
            Error.INVALID_OLD_ENTITY_PASSWORD to Field.SSS_USER_OLD_PASSWORD
        )
    }

    "Try to update user with invalid new password via $src" {
        val login = contract.buildUserLogin()
        val pass = "Strong13Password"
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", pass)
        contract.closeUser(login)
        contract.updateUserPasswordWithSamePass(
            login, pass,
            Error.INVALID_ENTITY_OLD_AND_NEW_PASSWORD to Field.SSS_USER_PASSWORD
        )
    }

    "Try to update user password with invalid status via $src" {
        val login = contract.buildUserLogin()
        val pass = "Strong13Password"
        contract.createUser(login, "$login$TEST_MAIL_DOMAIN", pass)
        contract.updateUserPasswordWithInvalidStatus(
            login, pass,
            Error.INVALID_ENTITY_STATUS to Field.SSS_USER_STATUS
        )
    }

}
