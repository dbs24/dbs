package org.dbs.tree.user

import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Suppress("unused")
class UsersGrpcTests : BaseTreeGrpcTest() {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    init {

        "Create user via $source" {
            createOrUpdateSuccess(buildUserRequest("validuser1", "valid_user@test.com", "Strong1Password"))
        }

        "Create another user via $source" {
            createOrUpdateSuccess(buildUserRequest("validuser2", "valid_user2@test.com", "Strong12Password"))
        }

        "Try to create invalid exists user via $source" {
            createOrUpdateUserWithValidationError(
                buildUserRequest(login = "validuser2", email = "valid_user2@test.com", password = "Strong12Password"),
                Error.ALREADY_EXISTS to Field.SSS_USER_LOGIN
            )
        }

        "Try to create invalid user via $source" {
            createOrUpdateUserWithValidationError(
                buildUserRequest(login = "", email = "", password = "fp"),
                Error.MANDATORY_FIELD_MISSING to Field.SSS_USER_LOGIN
            )
        }

        "Try to create invalid user with invalid login via $source" {
            createOrUpdateUserWithValidationError(
                buildUserRequest(login = "vali", email = "valid_user2@test.com", password = "Strong12Password"),
                Error.INVALID_ATTR_PATTERN_MISMATCH to Field.SSS_USER_LOGIN
            )
        }

        "Get user1 credentials via $source" {
            getUserCredentials(buildUserCredentialsRequest("validuser1"))
        }

        "Get user credentials with fail via $source" {
            getUserCredentialsWithInternalError(
                buildUserCredentialsRequest("validuser0"))
        }

        "Get user credentials with invalid login via $source" {
            getUserCredentialsWithFails(
                buildUserCredentialsRequest("validuser#1"),
            Error.INVALID_ATTR_PATTERN_MISMATCH to Field.SSS_USER_LOGIN)
        }

        "Get user2 credentials via $source" {
            getUserCredentials(buildUserCredentialsRequest("validuser2"))
        }

        "Try to close user with unknown status via $source" {
            updateUserStatusWithFail(buildUserStatusRequest("validuser1", "FAKED_STATUS"),
                Error.UNKNOWN_ENTITY_STATUS to Field.SSS_USER_STATUS)
        }

        "Close user via $source" {
            updateUserStatusSuccess(buildUserStatusRequest("validuser1", "CLOSED"))
        }

        "Close user2 via $source" {
            updateUserStatusSuccess(buildUserStatusRequest("validuser2", "CLOSED"))
        }

        "Try to close user with invalid status via $source" {
            updateUserStatusWithFail(buildUserStatusRequest("validuser1", "CLOSED"),
            Error.INVALID_ENTITY_STATUS to Field.SSS_USER_STATUS)
        }

        "Reopen user via $source" {
            updateUserStatusSuccess(buildUserStatusRequest("validuser1", "ACTUAL"))
        }

        "Update user password via $source" {
            updateUserPasswordSuccess(buildUserPasswordRequest("validuser1", "Strong1Password", "Strong2Password"))
        }


        "Try to update user with invalid password via $source" {
            updateUserPasswordWithFail(buildUserPasswordRequest("validuser1", "Strong1Password", "Strong2Password"),
                Error.INVALID_OLD_ENTITY_PASSWORD to Field.SSS_USER_OLD_PASSWORD)
        }

        "Try to update user with invalid new password via $source" {
            updateUserPasswordWithFail(buildUserPasswordRequest("validuser1", "Strong2Password", "Strong2Password"),
                Error.INVALID_ENTITY_OLD_AND_NEW_PASSWORD to Field.SSS_USER_PASSWORD)
        }

        "Try to update user password with invalid user status via $source" {
            updateUserPasswordWithFail(buildUserPasswordRequest("validuser2", "Strong12Password", "Strong2Password"),
                Error.INVALID_ENTITY_STATUS to Field.SSS_USER_STATUS)
        }
    }
}
