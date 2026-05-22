package org.dbs.tree.user

import io.grpc.ManagedChannel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.test.ko.BaseGrpcSpec
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.dbs.tree.client.CreateOrUpdateUserRequest as REQ_CREATE_USER
import org.dbs.tree.client.UserCredentialsRequest as REQ_GET_USER_CR

typealias Stub = UserServiceGrpcKt.UserServiceCoroutineStub

abstract class BaseTreeGrpcTest : BaseGrpcSpec() {

    @Autowired lateinit var userRepo: UserRepo
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userStub: Stub

    private val userFactory = GrpcEntityFactory(REQ_CREATE_USER::newBuilder, REQ_CREATE_USER.Builder::build)
    private val userCredentialFactory = GrpcEntityFactory(REQ_GET_USER_CR::newBuilder, REQ_GET_USER_CR.Builder::build)

    override fun initStubs(channel: ManagedChannel) {
        userStub = Stub(channel)
    }

    protected fun buildUserRequest(
        login: String,
        email: String,
        password: String = "",
        phone: String = "",
        firstName: String = "",
        lastName: String = "",
        middleName: String = "",
        oldLogin: String = "",
        oldEmail: String = ""
    ): REQ_CREATE_USER = userFactory.create {
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

    protected suspend fun createOrUpdateSuccess(request: REQ_CREATE_USER) {
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

    protected suspend fun createOrUpdateUserWithValidationError(request: REQ_CREATE_USER, vararg expectedErrors: Pair<Error, Field>) {
        suspend { userStub.createOrUpdateUser(request) }
            .shouldFailWithValidation()
            .shouldContainErrors(*expectedErrors)
    }

    protected suspend fun createOrUpdateUserWithInternalError(request: REQ_CREATE_USER) {
        suspend { userStub.createOrUpdateUser(request) }
            .shouldFailWithInternalError()
    }

    protected fun buildUserCredentialsRequest(login: String): REQ_GET_USER_CR =
        userCredentialFactory.create { setUserLogin(login) }

    protected suspend fun getUserCredentials(request: REQ_GET_USER_CR) {
        val response = userStub.getUserCredentials(request)

        response.userLogin shouldBe request.userLogin

        userRepo.findByLogin(request.userLogin).apply {
            response.userPassword shouldBe this?.password
        }
    }

    protected suspend fun getUserCredentialsWithInternalError(request: REQ_GET_USER_CR) {
        suspend { userStub.getUserCredentials(request) }
            .shouldFailWithInternalError()
    }

}