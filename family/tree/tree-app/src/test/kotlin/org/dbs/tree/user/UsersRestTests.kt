package org.dbs.tree.user

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.consts.PasswordNull
import org.dbs.test.ko.BaseRestSpec
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.UserCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
@Suppress("unused")
class UsersRestTests : BaseRestSpec() {

    override val requestMapping = "/users"

    @Autowired lateinit var userRepo: UserRepo
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    init {
        "Create user via $source" {
            val dto = createUserDto("rest_valid_user", "rest_valid_user@test.com", "rest_Strong1Password")

            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }
        }

        "Create another user via $source" {
            val dto = createUserDto("rest_valid_user1", "rest_valid_user1@test.com", "rest_Strong1Password")

            postQuery<CreateOrUpdateUserDto, CreatedUserDto>("/createOrUpdate", dto) { response ->
                assertCreatedUser(dto, response)
            }
        }

        "Create invalid exists user via $source" {
            val dto = createUserDto("rest_valid_user1", "rest_valid_user1@test.com", "rest_Strong1Password")

            postQueryShouldFail("/createOrUpdate", dto)
                .shouldContainErrors(Error.ALREADY_EXISTS to Field.SSS_LOGIN_USER)
        }

        "Try to create invalid user via $source" {
            val dto = createUserDto(login = "", email = "")

            postQueryShouldFail("/createOrUpdate", dto)
                .shouldContainErrors(Error.INVALID_DTO_ATTR to Field.SSS_LOGIN_USER)
        }
    }

    private fun createUserDto(
        login: EntityCode,
        email: Email,
        password: PasswordNull = null,
        phone: String? = null,
        firstName: String? = null,
        lastName: String? = null,
        middleName: String? = null,
    ) = CreateOrUpdateUserDto(
        oldLogin = null, login = login,
        oldEmail = null, email = email,
        phone = phone, firstName = firstName,
        lastName = lastName, middleName = middleName,
        password = password
    )

    private suspend fun assertCreatedUser(
        dto: CreateOrUpdateUserDto,
        response: CreatedUserDto
    ) {
        // Проверка HTTP Response
        response.status shouldBe ES_USER_ANONYMOUS.entityStatusName
        response.email shouldBe dto.email
        response.modifiedLogin shouldBe dto.login

        // Проверка всех полей Entity в БД
        verifyModifiedEntity(
            userRepo.findByLogin(dto.login),
            EA_CREATE_OR_UPDATE_USER,
            User::entityStatus verify { it shouldBe ES_USER_ANONYMOUS },
            User::userId verify { it shouldBe entityId },
            User::entityId verify { it shouldBe userId },
            User::birthDate verify { it shouldBe null },
            User::closeDate verify { it shouldBe null },
            User::createDate verify { it shouldNotBe null },
            User::modifyDate verify { it shouldBe createDate },
            User::login verify { it shouldBe dto.login },
            User::email verify { it shouldBe dto.email },
            User::phone verify { it shouldBe dto.phone },
            User::firstName verify { it shouldBe dto.firstName },
            User::middleName verify { it shouldBe dto.middleName },
            User::lastName verify { it shouldBe dto.lastName },
            User::password verify { passwordEncoder.matches(dto.password, it) },
        )
    }
}
