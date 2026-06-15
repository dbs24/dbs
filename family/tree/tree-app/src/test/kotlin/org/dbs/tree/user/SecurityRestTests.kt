package org.dbs.tree.user

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.user.dto.user.UpdateUserStatusDto
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
@Suppress("unused")
@Isolate
@Deprecated("use contract test")
class SecurityRestTests : BaseTreeRestTest() {

    override val requestMapping = "/security"
    private val newUserLogin: String  get() = "validrestuser$testNum"

    init {

        isolationMode = IsolationMode.InstancePerTest
        testCaseOrder = TestCaseOrder.Random
        //testExecutionMode = TestExecutionMode.Concurrent

        "Login user via $source" {

            val hotUserLogin = newUserLogin
            val hotUserPassword = "rest_Strong1Password$hotUserLogin"

            // create user
            val dto = createUserDto(hotUserLogin, "$hotUserLogin$TEST_MAIL_DOMAIN", hotUserPassword)
            createUser( dto)

            // update status
            val updateDto = UpdateUserStatusDto(hotUserLogin, ES_USER_ACTUAL.entityStatusName)
            updateUserStatus(updateDto)

            // login
            val loginDto = createLoginUserDto(hotUserLogin, hotUserPassword)
            loginUser(loginDto)

        }

        "Refresh user tokens via $source" {

            val hotUserLogin = newUserLogin
            val hotUserPassword = "rest_Strong1Password$hotUserLogin"

            // create user
            val dto = createUserDto(hotUserLogin, "$hotUserLogin$TEST_MAIL_DOMAIN", hotUserPassword)
            createUser( dto)

            // update status
            val updateDto = UpdateUserStatusDto(hotUserLogin, ES_USER_ACTUAL.entityStatusName)
            updateUserStatus(updateDto)

            // login
            val loginDto = createLoginUserDto(hotUserLogin, hotUserPassword)
            val tokens = loginUser(loginDto)

            // refresh
            val refreshTokenDto = createRefreshTokenDto(tokens)
            val newTokens = refresh(refreshTokenDto, hotUserLogin)

        }

        "Login user with fail via $source" {

            val hotUserLogin = newUserLogin
            val hotUserPassword = "rest_Strong1Password$hotUserLogin"

            // create user
            val dto = createUserDto(hotUserLogin, "$hotUserLogin$TEST_MAIL_DOMAIN", hotUserPassword)
            createUser( dto)

            // login with fail
            val loginDto = createLoginUserDto(hotUserLogin, hotUserPassword)
            loginUserWithErrors(loginDto)
                .shouldContainErrors(Error.INVALID_ENTITY_STATUS to Field.SSS_USER_STATUS)

        }

        "Refresh jwt with fail via $source" {

            val hotUserLogin = newUserLogin
            val hotUserPassword = "rest_Strong1Password$hotUserLogin"

            // create user
            val dto = createUserDto(hotUserLogin, "$hotUserLogin$TEST_MAIL_DOMAIN", hotUserPassword)
            createUser( dto)

            // update status
            val updateDto = UpdateUserStatusDto(hotUserLogin, ES_USER_ACTUAL.entityStatusName)
            updateUserStatus(updateDto)

            // login
            val loginDto = createLoginUserDto(hotUserLogin, hotUserPassword)
            val tokens = loginUser(loginDto)

            // refresh
            val refreshTokensDto = createRefreshTokenDto(tokens)
            val newTokens = refresh(refreshTokensDto, hotUserLogin)

            // refresh with invalid tokens
            refreshJwtsWithErrors(refreshTokensDto)
                .shouldContainErrors(Error.INVALID_JWT to Field.FLD_ACCESS_JWT)

        }
    }
}
