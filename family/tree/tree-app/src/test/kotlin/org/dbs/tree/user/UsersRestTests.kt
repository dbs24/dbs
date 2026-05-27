package org.dbs.tree.user

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
class UsersRestTests : BaseTreeRestTest() {

    override val requestMapping = "/users"

    init {
        "Create user via $source" {
            val dto = createUserDto("restvaliduser1", "rest_valid_user1@test.com", "rest_Strong1Password")

            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }
        }

        "Create another user via $source" {
            val dto = createUserDto("restvaliduser2", "rest_valid_user2@test.com", "rest_Strong2Password")

            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }
        }

        "Create invalid exists user via $source" {
            val dto = createUserDto("restvaliduser2", "rest_valid_user2@test.com", "rest_Strong2Password")

            postQueryShouldFailWithValidationError("/createOrUpdate", dto)
                .shouldContainErrors(Error.ALREADY_EXISTS to Field.SSS_USER_LOGIN)
        }

        "Try to create invalid user via $source" {
            val dto = createUserDto(login = "", email = "", password = "Strong12Password")

            postQueryShouldFailWithValidationError("/createOrUpdate", dto)

        }

        "Try to create user with invalid email via $source" {
            val dto = createUserDto(login = "restvaliduser3", email = "invalid_mail", password = "Strong12Password")

            postQueryShouldFailWithValidationError("/createOrUpdate", dto)

        }
    }
}
