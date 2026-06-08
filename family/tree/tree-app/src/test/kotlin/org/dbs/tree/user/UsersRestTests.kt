package org.dbs.tree.user

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_CLOSED
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
class UsersRestTests : BaseTreeRestTest() {

    override val requestMapping = "/users"
    private val newUserLogin: String  get() = "validrestuser$testNum"

    init {

        isolationMode = IsolationMode.InstancePerTest
        testCaseOrder = TestCaseOrder.Random
        //testExecutionMode = TestExecutionMode.Concurrent

        "Create user via $source" {

            val hotUserLogin = newUserLogin

            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong1Password")
            createUser(dto)

        }

        "Create another user via $source" {

            val hotUserLogin = newUserLogin
            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")

            createUser(dto)
        }

        "Create invalid exists user via $source" {

            val hotUserLogin = newUserLogin
            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")
            createUser(dto)

            val invalidDto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")

            postQueryShouldFailWithValidationError("/createOrUpdate", dto)
                .shouldContainErrors(Error.ALREADY_EXISTS to Field.SSS_USER_LOGIN)
        }

        "Try to create invalid user via $source" {
            val dto = createUserDto(login = "", email = "", password = "Strong12Password")
            postQueryShouldFailWithValidationError("/createOrUpdate", dto)

        }

        "Try to create user with invalid email via $source" {
            val hotUserLogin = newUserLogin
            val invalidEmail = "invalid_mail"
            val dto = createUserDto(login = hotUserLogin, email = invalidEmail, password = "Strong12Password")
            postQueryShouldFailWithValidationError("/createOrUpdate", dto)
        }

        "Close user via $source" {

            val hotUserLogin = newUserLogin
            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")

            createUser(dto)

            val updateDto = UpdateUserStatusDto(hotUserLogin, ES_USER_CLOSED.entityStatusName)
            updateUserStatus(updateDto)

        }
    }
}
