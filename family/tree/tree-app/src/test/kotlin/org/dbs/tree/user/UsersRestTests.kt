package org.dbs.tree.user

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
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

    init {

        isolationMode = IsolationMode.InstancePerTest
        testCaseOrder = TestCaseOrder.Random
        //testExecutionMode = TestExecutionMode.Concurrent

        "Create user via $source" {

            val hotUserLogin = "validrestuser1"

            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong1Password")

            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }
        }

        "Create another user via $source" {

            val hotUserLogin = "validrestuser2"
            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")

            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }
        }

        "Create invalid exists user via $source" {

            val hotUserLogin = "validrestuser3"
            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")
            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }

            val invalidDto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", "rest_Strong2Password")

            postQueryShouldFailWithValidationError("/createOrUpdate", dto)
                .shouldContainErrors(Error.ALREADY_EXISTS to Field.SSS_USER_LOGIN)
        }

        "Try to create invalid user via $source" {
            val dto = createUserDto(login = "", email = "", password = "Strong12Password")
            postQueryShouldFailWithValidationError("/createOrUpdate", dto)

        }

        "Try to create user with invalid email via $source" {
            val hotUserLogin = "validrestuser5"
            val invalidEmail = "invalid_mail"
            val dto = createUserDto(login = hotUserLogin, email = invalidEmail, password = "Strong12Password")
            postQueryShouldFailWithValidationError("/createOrUpdate", dto)
        }
    }
}
