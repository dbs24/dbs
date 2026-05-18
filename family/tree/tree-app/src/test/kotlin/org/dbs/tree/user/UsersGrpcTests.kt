package org.dbs.tree.user

import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.validator.Error
import org.dbs.validator.Error.INVALID_ATTR_PATTERN_MISMATCH
import org.dbs.validator.Error.MANDATORY_FIELD_MISSING
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
            createOrUpdateFail(
                buildUserRequest(login = "validuser2", email = "valid_user2@test.com", password = "Strong12Password"),
                Error.ALREADY_EXISTS to Field.SSS_USER_LOGIN
            )
        }

        "Try to create invalid user via $source" {
            createOrUpdateFail(
                buildUserRequest(login = "", email = "", password = "fp"),
                MANDATORY_FIELD_MISSING to Field.SSS_USER_LOGIN,
                INVALID_ATTR_PATTERN_MISMATCH to Field.SSS_USER_PASSWORD,
            )
        }

        "Get user credentials via $source" {
            getUserCredentials(buildUserCredentialsRequest("validuser2"))
        }

    }

}
