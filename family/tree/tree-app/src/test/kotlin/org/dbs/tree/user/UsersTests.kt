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
import org.dbs.validator.Field
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder
import org.dbs.tree.client.CreateOrUpdateUserRequest as REQ

typealias Stub = UserServiceGrpcKt.UserServiceCoroutineStub

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
@Suppress("unused")
class UsersGrpcTests : BaseGrpcSpec() {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userStub: Stub

    override fun initStubs(channel: ManagedChannel) {
        userStub = Stub(channel)
    }

    init {

        "Success: Create user via gRPC network call" {

            val testedLogin = "valid_user"
            val testedMail = "valid_user@test.com"
            val testedPassword = "Strong1Password"

            val request = REQ.newBuilder()
                .setLogin(testedLogin)
                .setEmail(testedMail)
                .setPassword(testedPassword)
                .build()

            userStub.createOrUpdateUser(request).apply {
                userLogin shouldBe testedLogin
                email shouldBe testedMail
                status shouldBe ES_USER_ANONYMOUS.entityStatusName
            }

            verifyModifiedEntity(
                userRepo.findByLogin(testedLogin),
                EA_CREATE_OR_UPDATE_USER,
                // validate fields
                User::entityStatus verify { it shouldBe ES_USER_ANONYMOUS },
                User::userId verify { it shouldBe entityId },
                User::entityId verify { it shouldBe userId },
                User::birthDate verify { it shouldBe null },
                User::closeDate verify { it shouldBe null },
                User::createDate verify { it shouldNotBe null },
                User::modifyDate verify { it shouldBe createDate },
                User::login verify { it shouldBe testedLogin },
                User::email verify { it shouldBe testedMail },
                User::phone verify { it shouldBe null },
                User::firstName verify { it shouldBe null },
                User::middleName verify { it shouldBe null },
                User::lastName verify { it shouldBe null },
                User::password verify { passwordEncoder.matches(testedPassword, it) },
            )
        }

        "Success: Try to create invalid user via gRPC network call" {
            val request = REQ.newBuilder()
                .setLogin("")
                .build()

            suspend { userStub.createOrUpdateUser(request) }
                .shouldFailWithValidation()
                .shouldContainErrors(
                    Error.INVALID_DTO_ATTR to Field.SSS_LOGIN_USER,
                    Error.INVALID_DTO_ATTR to Field.SSS_LOGIN_USER,
                )
        }
    }
}
