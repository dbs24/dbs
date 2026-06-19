package org.dbs.tree.user

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.consts.FieldError
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.test.ko.BaseSpec.PropertyValidator
import org.dbs.tree.BaseTreeGrpcTest
import org.dbs.tree.model.user.User
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.EntityTypes.ET_USER
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_UPDATE_USER_PASSWORD
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_UPDATE_USER_STATUS
import org.dbs.user.FamilyTreeCore.isClosedUser
import org.dbs.validator.Field
import org.dbs.tree.client.CreateOrUpdateUserRequest as USER
import org.dbs.tree.client.UpdateUserPasswordRequest as PASSWORD
import org.dbs.tree.client.UpdateUserStatusRequest as STATUS
import org.dbs.tree.client.UserCredentialsRequest as CREDS

object UserGrpcFuncs {

    fun BaseTreeGrpcTest.buildUserRequest(
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

    fun BaseTreeGrpcTest.buildUserCredentialsRequest(login: String): CREDS =
        CREDS.newBuilder().build { setUserLogin(login) }

    fun BaseTreeGrpcTest.buildUserStatusRequest(login: String, newStatus: EntityStatus): STATUS =
        STATUS.newBuilder().build { setModifiedLogin(login); setStatus(newStatus.entityStatusName) }

    fun BaseTreeGrpcTest.buildUserStatusRequest(login: String): STATUS =
        STATUS.newBuilder().build { setModifiedLogin(login); setStatus("FAKED_STATUS") }

    fun BaseTreeGrpcTest.buildUserPasswordRequest(login: String, oldP: String, newP: String): PASSWORD =
        PASSWORD.newBuilder().build { setModifiedLogin(login); setOldPassword(oldP); setNewPassword(newP) }

    suspend fun BaseTreeGrpcTest.createOrUpdateUserSuccess(req: USER): User = runCall { userStub.createOrUpdateUser(req) }
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
                User::modifyDate verify { if (req.oldLogin == "") it shouldBe createDate else it shouldBeGreaterThan createDate },
                User::login verify { it shouldBe req.login },
                User::email verify { it shouldBe req.email },
                User::phone verify { it shouldBe req.phone.takeIf { it.isNotEmpty() } },
                User::firstName verify { it shouldBe req.firstName.takeIf { it.isNotEmpty() } },
                User::middleName verify { it shouldBe req.middleName.takeIf { it.isNotEmpty() } },
                User::lastName verify { it shouldBe req.lastName.takeIf { it.isNotEmpty() } },
                User::password verify { passwordEncoder.matches(req.password, it) },
            )

            verifyModifiedEntity(
                userRepo.findByLogin(req.login), EA_CREATE_OR_UPDATE_USER, verifyAllFields = true, *userValidators
            )

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

    suspend fun BaseTreeGrpcTest.getUserCredentials(req: CREDS) {
        val res = userStub.getUserCredentials(req)
        res.userLogin shouldBe req.userLogin
        userRepo.findByLogin(req.userLogin)?.let { res.userPassword shouldBe it.password }
    }

    suspend fun BaseTreeGrpcTest.updateUserStatusSuccess(req: STATUS) = runCall { userStub.updateUserStatus(req) }
        .shouldSuccess { res ->
            res.modifiedLogin shouldBe req.modifiedLogin
            res.newStatus shouldBe req.status

            val statusEnum = EntityStatusEnum.findStatus<EntityStatus>(req.status, ET_USER)
                ?: error("Status not found: ${req.status}, entityType: $ET_USER")
            val isClosed = isClosedUser(statusEnum)

            verifyModifiedEntity(
                userRepo.findByLogin(req.modifiedLogin), EA_UPDATE_USER_STATUS, verifyAllFields = false,
                User::entityStatus verify { it shouldBe statusEnum },
                User::closeDate verify { it shouldBe if (isClosed) modifyDate else null }
            )
        }

    suspend fun BaseTreeGrpcTest.updateUserPasswordSuccess(req: PASSWORD) = runCall { userStub.updateUserPassword(req) }
        .shouldSuccess { res ->
            res.modifiedLogin shouldBe req.modifiedLogin
            verifyModifiedEntity(
                userRepo.findByLogin(req.modifiedLogin), EA_UPDATE_USER_PASSWORD, verifyAllFields = false,
                User::password verify { passwordEncoder.matches(req.newPassword, it).shouldBeEqual(true) },
            )
        }

    // --- Тестовые шаги: Обработка ошибок ---
    suspend fun BaseTreeGrpcTest.createOrUpdateUserWithValidationError(req: USER, vararg errs: FieldError) =
        runCall { userStub.createOrUpdateUser(req) }.shouldFailWithValidation().shouldContainErrors(*errs)

    suspend fun BaseTreeGrpcTest.createOrUpdateUserWithInternalError(req: USER) =
        runCall { userStub.createOrUpdateUser(req) }.shouldFailWithInternalError()

    suspend fun BaseTreeGrpcTest.getUserCredentialsWithInternalError(req: CREDS) =
        runCall { userStub.getUserCredentials(req) }.shouldFailWithInternalError()

    suspend fun BaseTreeGrpcTest.getUserCredentialsWithFails(req: CREDS, vararg errs: Pair<org.dbs.validator.Error, Field>) =
        runCall { userStub.getUserCredentials(req) }.shouldFailWithValidation().shouldContainErrors(*errs)

    suspend fun BaseTreeGrpcTest.updateUserStatusWithFail(req: STATUS, vararg errs: Pair<org.dbs.validator.Error, Field>) =
        runCall { userStub.updateUserStatus(req) }.shouldFailWithValidation().shouldContainErrors(*errs)

    suspend fun BaseTreeGrpcTest.updateUserPasswordWithFail(req: PASSWORD, vararg errs: FieldError) =
        runCall { userStub.updateUserPassword(req) }.shouldFailWithValidation().shouldContainErrors(*errs)
}