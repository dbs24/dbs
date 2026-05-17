package org.dbs.tree.user

import io.grpc.ManagedChannel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.test.ko.BaseGrpcSpec
import org.dbs.tree.TreeApplication
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.UserCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.UserCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.validator.Error
import org.dbs.validator.Error.INVALID_ATTR_PATTERN_MISMATCH
import org.dbs.validator.Error.MANDATORY_FIELD_MISSING
import org.dbs.validator.Field
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.dbs.tree.client.CreateOrUpdateUserRequest as REQ

typealias Stub = UserServiceGrpcKt.UserServiceCoroutineStub

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Suppress("unused")
class UsersGrpcTests : BaseGrpcSpec() {

    @Autowired lateinit var userRepo: UserRepo
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userStub: Stub

    val userFactory = GrpcEntityFactory(REQ::newBuilder, REQ.Builder::build)

    override fun initStubs(channel: ManagedChannel) {
        userStub = Stub(channel)
    }
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun buildUserRequest(
        login: String,
        email: String,
        password: String = "",
        phone: String = "",
        firstName: String = "",
        lastName: String = "",
        middleName: String = "",
        oldLogin: String = "",
        oldEmail: String = ""
    ): REQ = userFactory.create {
        setLogin(login)
        setEmail(email)
        if (password.isNotEmpty()) setPassword(password)
        if (phone.isNotEmpty()) setPhone(phone)
        if (firstName.isNotEmpty()) setFirstName(firstName)
        if (lastName.isNotEmpty()) setLastName(lastName)
        if (middleName.isNotEmpty()) setMiddleName(middleName)
        if (oldLogin.isNotEmpty()) setOldLogin(oldLogin)
        if (oldEmail.isNotEmpty()) setOldEmail(oldEmail)
    }

    private suspend fun createOrUpdateSuccess(request: REQ) {
        val response = userStub.createOrUpdateUser(request)

        response.userLogin shouldBe request.login
        response.email shouldBe request.email
        response.status shouldBe ES_USER_ANONYMOUS.entityStatusName

        verifyModifiedEntity(
            userRepo.findByLogin(request.login),
            EA_CREATE_OR_UPDATE_USER,
            User::entityStatus verify { it shouldBe ES_USER_ANONYMOUS },
            User::userId verify { it shouldBe entityId },
            User::entityId verify { it shouldBe userId },
            User::birthDate verify { it shouldBe null },
            User::closeDate verify { it shouldBe null },
            User::createDate verify { it shouldNotBe null },
            User::modifyDate verify { it shouldBe createDate },
            User::login verify { it shouldBe request.login },
            User::email verify { it shouldBe request.email },
            User::phone verify { it shouldBe request.phone.takeIf { it.isNotEmpty() } },
            User::firstName verify { it shouldBe request.firstName.takeIf { it.isNotEmpty() } },
            User::middleName verify { it shouldBe request.middleName.takeIf { it.isNotEmpty() } },
            User::lastName verify { it shouldBe request.lastName.takeIf { it.isNotEmpty() } },
            User::password verify { passwordEncoder.matches(request.password, it) },
        )
    }

    private suspend fun createOrUpdateFail(request: REQ, vararg expectedErrors: Pair<Error, Field>) {
        suspend { userStub.createOrUpdateUser(request) }
            .shouldFailWithValidation()
            .shouldContainErrors(*expectedErrors)
    }

}
