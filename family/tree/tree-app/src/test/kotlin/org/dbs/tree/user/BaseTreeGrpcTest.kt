package org.dbs.tree.user

import io.grpc.ManagedChannel
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import com.google.protobuf.MessageLite
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.test.ko.BaseGrpcSpec
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.UserActionEnum.*
import org.dbs.user.FamilyTreeCore.isClosedUser
import org.dbs.validator.Error
import org.dbs.validator.Field
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.dbs.tree.client.CreateOrUpdateUserRequest as USER
import org.dbs.tree.client.UpdateUserPasswordRequest as PASSWORD
import org.dbs.tree.client.UpdateUserStatusRequest as STATUS
import org.dbs.tree.client.UserCredentialsRequest as CREDS

typealias Stub = UserServiceGrpcKt.UserServiceCoroutineStub

abstract class BaseTreeGrpcTest : BaseGrpcSpec() {

    @Autowired lateinit var userRepo: UserRepo
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userStub: Stub

    override fun initStubs(channel: ManagedChannel) {
        userStub = Stub(channel)
    }

    // --- Высокопроизводительные хелперы билдеров (Без рефлексии) ---
    private inline fun <B : MessageLite.Builder, M : MessageLite> B.build(block: B.() -> Unit): M =
        apply(block).build() as M

    // --- Билдеры запросов ---
    protected fun buildUserRequest(
        login: String, email: String, password: String = "", phone: String = "",
        firstName: String = "", lastName: String = "", middleName: String = "",
        oldLogin: String = "", oldEmail: String = ""
    ): USER = USER.newBuilder().build {
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

    protected fun buildUserCredentialsRequest(login: String): CREDS =
        CREDS.newBuilder().build { setUserLogin(login) }

    protected fun buildUserStatusRequest(login: String, newStatus: String): STATUS =
        STATUS.newBuilder().build { setModifiedLogin(login); setStatus(newStatus) }

    protected fun buildUserPasswordRequest(login: String, oldP: String, newP: String): PASSWORD =
        PASSWORD.newBuilder().build { setModifiedLogin(login); setOldPassword(oldP); setNewPassword(newP) }

    // --- Тестовые шаги: Успешные сценарии ---
    protected suspend fun createOrUpdateSuccess(req: USER) = runCall { userStub.createOrUpdateUser(req) }
        .shouldSuccess { res ->
            res.userLogin shouldBe req.login
            res.email shouldBe req.email
            res.status shouldBe ES_USER_ANONYMOUS.entityStatusName

            val userValidators: Array<PropertyValidator<User, *>> = arrayOf(
                User::entityStatus verify { it shouldBe ES_USER_ANONYMOUS },
                User::userId verify { it shouldBe entityId },
                User::entityId verify { it shouldBe userId },
                User::birthDate verify { it shouldBe null },
                User::closeDate verify { it shouldBe null },
                User::createDate verify { it shouldNotBe null },
                User::modifyDate verify { it shouldBe createDate },
                User::login verify { it shouldBe req.login },
                User::email verify { it shouldBe req.email },
                User::phone verify { it shouldBe req.phone.takeIf { it.isNotEmpty() } },
                User::firstName verify { it shouldBe req.firstName.takeIf { it.isNotEmpty() } },
                User::middleName verify { it shouldBe req.middleName.takeIf { it.isNotEmpty() } },
                User::lastName verify { it shouldBe req.lastName.takeIf { it.isNotEmpty() } },
                User::password verify { passwordEncoder.matches(req.password, it) },
            )

            verifyModifiedEntity(
                userRepo.findByLogin(req.login), EA_CREATE_OR_UPDATE_USER, verifyAllFields = true, *userValidators)

//            verifyModifiedEntity2(
//                userRepo.findByLogin(req.login), EA_CREATE_OR_UPDATE_USER, verifyAllFields = true,
//                "entityStatus" to { entityStatus verifyThat { this shouldBe ES_USER_ANONYMOUS } },
//                "userId" to {  userId verifyThat { this shouldBe entityId }},
//                "birthDate" to { birthDate verifyThat { this shouldBe null }},
//                "closeDate" to { closeDate verifyThat { this shouldBe null }},
//                "createDate" to { createDate verifyThat { this shouldNotBe null }},
//                "modifyDate" to { modifyDate verifyThat { this shouldBe createDate }},
//                "login" to { login verifyThat { this shouldBe req.login }},
//                "email" to { email verifyThat { this shouldBe req.email }},
//                "phone" to { phone verifyThat { this shouldBe req.phone.takeIf { it.isNotEmpty() } }},
//                "firstName" to { firstName verifyThat { this shouldBe req.firstName.takeIf { it.isNotEmpty() } }},
//                "middleName" to { middleName verifyThat { this shouldBe req.middleName.takeIf { it.isNotEmpty() } }},
//                "lastName" to { lastName verifyThat { this shouldBe req.lastName.takeIf { it.isNotEmpty() } }},
//                "password" to { password verifyThat { passwordEncoder.matches(req.password, this) }},
//
//            )

        }

    protected suspend fun getUserCredentials(req: CREDS) {
        val res = userStub.getUserCredentials(req)
        res.userLogin shouldBe req.userLogin
        userRepo.findByLogin(req.userLogin)?.let { res.userPassword shouldBe it.password }
    }

    protected suspend fun updateUserStatusSuccess(req: STATUS) = runCall { userStub.updateUserStatus(req) }
        .shouldSuccess { res ->
            res.modifiedLogin shouldBe req.modifiedLogin
            res.newStatus shouldBe req.status

            val statusEnum = EntityStatusEnum.findStatus<EntityStatus>(req.status)!!
            val isClosed = isClosedUser(statusEnum)

            verifyModifiedEntity(
                userRepo.findByLogin(req.modifiedLogin), EA_UPDATE_USER_STATUS, verifyAllFields = false,
                User::entityStatus verify { it shouldBe statusEnum },
                User::closeDate verify { it shouldBe if (isClosed) modifyDate else null }
            )
        }

    protected suspend fun updateUserPasswordSuccess(req: PASSWORD) = runCall { userStub.updateUserPassword(req) }
        .shouldSuccess { res ->
            res.modifiedLogin shouldBe req.modifiedLogin
            verifyModifiedEntity(
                userRepo.findByLogin(req.modifiedLogin), EA_UPDATE_USER_PASSWORD, verifyAllFields = false,
                User::password verify { passwordEncoder.matches(req.newPassword, it).shouldBeEqual(true) },
            )
        }

    // --- Тестовые шаги: Обработка ошибок ---
    protected suspend fun createOrUpdateUserWithValidationError(req: USER, vararg errs: Pair<Error, Field>) =
        runCall { userStub.createOrUpdateUser(req) }.shouldFailWithValidation().shouldContainErrors(*errs)

    protected suspend fun createOrUpdateUserWithInternalError(req: USER) =
        runCall { userStub.createOrUpdateUser(req) }.shouldFailWithInternalError()

    protected suspend fun getUserCredentialsWithInternalError(req: CREDS) =
        runCall { userStub.getUserCredentials(req) }.shouldFailWithInternalError()

    protected suspend fun getUserCredentialsWithFails(req: CREDS, vararg errs: Pair<Error, Field>) =
        runCall { userStub.getUserCredentials(req) }.shouldFailWithValidation().shouldContainErrors(*errs)

    protected suspend fun updateUserStatusWithFail(req: STATUS, vararg errs: Pair<Error, Field>) =
        runCall { userStub.updateUserStatus(req) }.shouldFailWithValidation().shouldContainErrors(*errs)

    protected suspend fun updateUserPasswordWithFail(req: PASSWORD, vararg errs: Pair<Error, Field>) =
        runCall { userStub.updateUserPassword(req) }.shouldFailWithValidation().shouldContainErrors(*errs)
}
