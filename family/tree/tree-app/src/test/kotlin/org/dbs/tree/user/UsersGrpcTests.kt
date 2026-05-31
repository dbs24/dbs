package org.dbs.tree.user

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.dbs.tree.BaseTreeGrpcTest
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
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
import org.dbs.validator.Error.ALREADY_EXISTS
import org.dbs.validator.Error.INVALID_ATTR_PATTERN_MISMATCH
import org.dbs.validator.Error.INVALID_ENTITY_OLD_AND_NEW_PASSWORD
import org.dbs.validator.Error.INVALID_ENTITY_STATUS
import org.dbs.validator.Error.INVALID_OLD_ENTITY_PASSWORD
import org.dbs.validator.Error.MANDATORY_FIELD_MISSING
import org.dbs.validator.Error.UNKNOWN_ENTITY_STATUS
import org.dbs.validator.Error.USER_DOES_NOT_EXISTS
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.validator.Field.SSS_USER_OLD_PASSWORD
import org.dbs.validator.Field.SSS_USER_PASSWORD
import org.dbs.validator.Field.SSS_USER_STATUS
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Suppress("unused")
@Isolate
class UsersGrpcTests : BaseTreeGrpcTest() {

    private val newUserLogin: String  get() = "validgrpcuser$testNum"

    init {

        val viaSource = " via $source"
        isolationMode = IsolationMode.InstancePerTest
        testCaseOrder = TestCaseOrder.Random
//        testExecutionMode = TestExecutionMode.Concurrent

        "Create user$viaSource" {

            val hotUserLogin = newUserLogin

            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong1Password"))
        }

        "Create another user$viaSource" {

            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
        }

        "Try to create invalid exists user$viaSource" {

            val hotUserLogin = newUserLogin

            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))

            createOrUpdateUserWithValidationError(
                buildUserRequest(login = hotUserLogin, email = "$hotUserLogin@test.com", password = "Strong12Password"),
                ALREADY_EXISTS to SSS_USER_LOGIN
            )
        }

        "Try to create invalid user$viaSource" {
            createOrUpdateUserWithValidationError(
                buildUserRequest(login = "", email = "", password = "fp"),
                MANDATORY_FIELD_MISSING to SSS_USER_LOGIN
            )
        }

        "Try to create invalid user with invalid login$viaSource" {
            createOrUpdateUserWithValidationError(
                buildUserRequest(login = "vali", email = "valid_user2@test.com", password = "Strong12Password"),
                INVALID_ATTR_PATTERN_MISMATCH to SSS_USER_LOGIN
            )
        }

        "Update user $viaSource" {

            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))

            createOrUpdateUserSuccess(buildUserRequest(login = hotUserLogin, oldLogin = hotUserLogin,
                email = "$hotUserLogin@test.com", password = "Strong12Password", firstName = "firstName"))

        }

        "Get user1 credentials$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "ACTUAL"))
            getUserCredentials(buildUserCredentialsRequest(hotUserLogin))
        }

        "Get user credentials with fail$viaSource" {
            getUserCredentialsWithFails(
                buildUserCredentialsRequest("invalidgrpcuser0"),
                USER_DOES_NOT_EXISTS to SSS_USER_LOGIN
            )
        }

        "Get user credentials with invalid login$viaSource" {
            getUserCredentialsWithFails(
                buildUserCredentialsRequest("loginNotExists#1"),
                INVALID_ATTR_PATTERN_MISMATCH to SSS_USER_LOGIN
            )
        }

        "Get user2 credentials$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "ACTUAL"))
            getUserCredentials(buildUserCredentialsRequest(hotUserLogin))
        }

        "Try to close user with unknown status$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))

            updateUserStatusWithFail(
                buildUserStatusRequest(login = hotUserLogin, newStatus = "FAKED_STATUS"),
                UNKNOWN_ENTITY_STATUS to SSS_USER_STATUS
            )
        }

        "Close user$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "CLOSED"))
        }

        "Close user2$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "CLOSED"))
        }

        "Try to close user with invalid status$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "CLOSED"))
            updateUserStatusWithFail(
                buildUserStatusRequest(login = hotUserLogin, newStatus = "CLOSED"),
                INVALID_ENTITY_STATUS to SSS_USER_STATUS
            )
        }

        "Reopen user$viaSource" {
            val hotUserLogin = newUserLogin
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", "Strong12Password"))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "CLOSED"))

            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "ACTUAL"))
        }

        "Update user password$viaSource" {
            val hotUserLogin = newUserLogin
            val password = "Strong12Password"
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", password))
            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "ACTUAL"))

            updateUserPasswordSuccess(
                buildUserPasswordRequest(login = hotUserLogin, oldP = password, newP = "Strong22Password")
            )
        }

        "Try to update user with invalid password$viaSource" {

            val hotUserLogin = newUserLogin
            val password = "Strong12Password"
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", password))

            updateUserPasswordWithFail(
                buildUserPasswordRequest(login = hotUserLogin, oldP = password+"1", newP = "Strong2Password"),
                INVALID_OLD_ENTITY_PASSWORD to SSS_USER_OLD_PASSWORD
            )
        }

        "Try to update user with invalid new password$viaSource" {

            val hotUserLogin = newUserLogin
            val password = "Strong13Password"
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", password))

            updateUserStatusSuccess(buildUserStatusRequest(login = hotUserLogin, newStatus = "CLOSED"))

            updateUserPasswordWithFail(
                buildUserPasswordRequest(login = hotUserLogin, oldP = password, newP = password),
                INVALID_ENTITY_OLD_AND_NEW_PASSWORD to SSS_USER_PASSWORD
            )
        }

        "Try to update user password with invalid user status$viaSource" {

            val hotUserLogin = newUserLogin
            val password = "Strong13Password"
            createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin@test.com", password))

            updateUserPasswordWithFail(
                buildUserPasswordRequest(login = hotUserLogin, oldP = password, newP = password),
                INVALID_ENTITY_STATUS to SSS_USER_STATUS
            )
        }
    }
}
