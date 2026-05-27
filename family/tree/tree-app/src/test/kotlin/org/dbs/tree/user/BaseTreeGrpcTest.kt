package org.dbs.tree.user

import io.grpc.ManagedChannel
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.test.ko.BaseGrpcSpec
import org.dbs.tree.client.UserServiceGrpcKt
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_UPDATE_USER_PASSWORD
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_UPDATE_USER_STATUS
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

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userStub: Stub

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
    ): USER = buildGrpcRequest<USER, USER.Builder> {
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

    protected suspend fun createOrUpdateSuccess(request: USER) {

        suspend { userStub.createOrUpdateUser(request) }
            .shouldSuccess { response ->

                response.userLogin shouldBe request.login
                response.email shouldBe request.email
                response.status shouldBe ES_USER_ANONYMOUS.entityStatusName

                verifyModifiedEntity(
                    userRepo.findByLogin(request.login),
                    EA_CREATE_OR_UPDATE_USER,
                    verifyAllFields = true,
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
    }

    protected suspend fun createOrUpdateUserWithValidationError(
        request: USER,
        vararg expectedErrors: Pair<Error, Field>
    ) {
        suspend { userStub.createOrUpdateUser(request) }
            .shouldFailWithValidation()
            .shouldContainErrors(*expectedErrors)
    }

    protected suspend fun createOrUpdateUserWithInternalError(request: USER) {
        suspend { userStub.createOrUpdateUser(request) }
            .shouldFailWithInternalError()
    }

    protected fun buildUserCredentialsRequest(login: String): CREDS =
        buildGrpcRequest<CREDS, CREDS.Builder>
        { setUserLogin(login) }

    protected suspend fun getUserCredentials(request: CREDS) {
        val response = userStub.getUserCredentials(request)

        response.userLogin shouldBe request.userLogin

        userRepo.findByLogin(request.userLogin).apply {
            response.userPassword shouldBe this?.password
        }
    }

    protected suspend fun getUserCredentialsWithInternalError(request: CREDS) {
        suspend { userStub.getUserCredentials(request) }
            .shouldFailWithInternalError()
    }

    protected suspend fun getUserCredentialsWithFails(
        request: CREDS,
        vararg expectedErrors: Pair<Error, Field>
    ) {
        suspend { userStub.getUserCredentials(request) }
            .shouldFailWithValidation()
            .shouldContainErrors(*expectedErrors)
    }

    protected fun buildUserStatusRequest(
        login: String,
        newStatus: String,
    ): STATUS = buildGrpcRequest<STATUS, STATUS.Builder> {
            setModifiedLogin(login)
            setStatus(newStatus)
        }

    protected suspend fun updateUserStatusSuccess(request: STATUS) {

        suspend { userStub.updateUserStatus(request) }
            .shouldSuccess { response ->

                response.modifiedLogin shouldBe request.modifiedLogin
                response.newStatus shouldBe request.status

                val newEnumStatus = EntityStatusEnum.findStatus<EntityStatus>(request.status)
                val isClosedStatus = isClosedUser(newEnumStatus!!)

                verifyModifiedEntity(
                    userRepo.findByLogin(request.modifiedLogin),
                    EA_UPDATE_USER_STATUS,
                    verifyAllFields = false,
                    User::entityStatus verify { it shouldBe newEnumStatus },
                    User::closeDate verify { it shouldBe if (isClosedStatus) modifyDate else null }
                )
            }
    }

    protected suspend fun updateUserStatusWithFail(
        request: STATUS,
        vararg expectedErrors: Pair<Error, Field>
    ) {

        suspend { userStub.updateUserStatus(request) }
            .shouldFailWithValidation()
            .shouldContainErrors(*expectedErrors)
    }

    protected fun buildUserPasswordRequest(
        login: String,
        oldPassword: String,
        newPassword: String,
    ): PASSWORD = buildGrpcRequest<PASSWORD, PASSWORD.Builder> {
        setModifiedLogin(login)
        setOldPassword(oldPassword)
        setNewPassword(newPassword)
    }

    protected suspend fun updateUserPasswordSuccess(request: PASSWORD) {
        suspend { userStub.updateUserPassword(request) }
            .shouldSuccess { response ->

                response.modifiedLogin shouldBe request.modifiedLogin

                verifyModifiedEntity(
                    userRepo.findByLogin(request.modifiedLogin),
                    EA_UPDATE_USER_PASSWORD,
                    verifyAllFields = false,
                    User::password verify {
                        passwordEncoder.matches(request.newPassword, it).shouldBeEqual(true)
                    },
                )
            }
    }

    protected suspend fun updateUserPasswordWithFail(
        request: PASSWORD,
        vararg expectedErrors: Pair<Error, Field>
    ) {

        suspend { userStub.updateUserPassword(request) }
            .shouldFailWithValidation()
            .shouldContainErrors(*expectedErrors)
    }

}