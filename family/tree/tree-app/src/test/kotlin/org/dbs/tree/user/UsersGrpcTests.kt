package org.dbs.tree.user

import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.validator.Error.*
import org.dbs.validator.Field.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Suppress("unused")
class UsersGrpcTests : BaseTreeGrpcTest() {

    init {
        // Локальное кэширование суффикса для ускорения сборки строковых имен тестов в init-блоке
        val viaSource = " via $source"

        "Create user$viaSource" {
            createOrUpdateSuccess(buildUserRequest("validuser1", "valid_user@test.com", "Strong1Password"))
        }

        "Create another user$viaSource" {
            createOrUpdateSuccess(buildUserRequest("validuser2", "valid_user2@test.com", "Strong12Password"))
        }

        "Try to create invalid exists user$viaSource" {
            createOrUpdateUserWithValidationError(
                buildUserRequest(login = "validuser2", email = "valid_user2@test.com", password = "Strong12Password"),
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

        "Get user1 credentials$viaSource" {
            getUserCredentials(buildUserCredentialsRequest("validuser1"))
        }

        "Get user credentials with fail$viaSource" {
            getUserCredentialsWithInternalError(buildUserCredentialsRequest("validuser0"))
        }

        "Get user credentials with invalid login$viaSource" {
            getUserCredentialsWithFails(
                buildUserCredentialsRequest("validuser#1"),
                INVALID_ATTR_PATTERN_MISMATCH to SSS_USER_LOGIN
            )
        }

        "Get user2 credentials$viaSource" {
            getUserCredentials(buildUserCredentialsRequest("validuser2"))
        }

        "Try to close user with unknown status$viaSource" {
            updateUserStatusWithFail(
                buildUserStatusRequest(login = "validuser1", newStatus = "FAKED_STATUS"),
                UNKNOWN_ENTITY_STATUS to SSS_USER_STATUS
            )
        }

        "Close user$viaSource" {
            updateUserStatusSuccess(buildUserStatusRequest(login = "validuser1", newStatus = "CLOSED"))
        }

        "Close user2$viaSource" {
            updateUserStatusSuccess(buildUserStatusRequest(login = "validuser2", newStatus = "CLOSED"))
        }

        "Try to close user with invalid status$viaSource" {
            updateUserStatusWithFail(
                buildUserStatusRequest(login = "validuser1", newStatus = "CLOSED"),
                INVALID_ENTITY_STATUS to SSS_USER_STATUS
            )
        }

        "Reopen user$viaSource" {
            updateUserStatusSuccess(buildUserStatusRequest(login = "validuser1", newStatus = "ACTUAL"))
        }

        "Update user password$viaSource" {
            updateUserPasswordSuccess(
                buildUserPasswordRequest(login = "validuser1", oldP = "Strong1Password", newP = "Strong22Password")
            )
        }

        "Try to update user with invalid password$viaSource" {
            updateUserPasswordWithFail(
                buildUserPasswordRequest(login = "validuser1", oldP = "Strong1Password", newP = "Strong2Password"),
                INVALID_OLD_ENTITY_PASSWORD to SSS_USER_OLD_PASSWORD
            )
        }

        "Try to update user with invalid new password$viaSource" {
            updateUserPasswordWithFail(
                buildUserPasswordRequest(login = "validuser1", oldP = "Strong2Password", newP = "Strong2Password"),
                INVALID_ENTITY_OLD_AND_NEW_PASSWORD to SSS_USER_PASSWORD
            )
        }

        "Try to update user password with invalid user status$viaSource" {
            updateUserPasswordWithFail(
                buildUserPasswordRequest(login = "validuser2", oldP = "Strong12Password", newP = "Strong2Password"),
                INVALID_ENTITY_STATUS to SSS_USER_STATUS
            )
        }
    }
}
