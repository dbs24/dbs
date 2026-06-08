package org.dbs.tree.user

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.consts.PasswordNull
import org.dbs.dto.jwt.LoginUserDto
import org.dbs.dto.jwt.LoginUserResponseDto
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.model.IssuedJwt
import org.dbs.model.RefreshJwt
import org.dbs.repo.AccessJwtRepo
import org.dbs.repo.RefreshJwtRepo
import org.dbs.test.ko.BaseRestSpec
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.UserRepo
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.EntityTypes.ET_USER
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_UPDATE_USER_STATUS
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.dbs.user.dto.user.UpdateUserStatusDto
import org.dbs.user.dto.user.UpdatedUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

interface TestAccessJwtRepo : AccessJwtRepo {
    suspend fun findByJwt(jwt: String): IssuedJwt?
}

interface TestRefreshJwtRepo : RefreshJwtRepo {
    suspend fun findByJwt(jwt: String): RefreshJwt?
}

abstract class BaseTreeRestTest: BaseRestSpec() {

    @Autowired lateinit var userRepo: UserRepo
    @Autowired lateinit var passwordEncoder: PasswordEncoder
    @Autowired lateinit var accessJwtRepo: TestAccessJwtRepo
    @Autowired lateinit var refreshJwtRepo: TestRefreshJwtRepo

    protected fun createUserDto(
        login: String,
        email: String,
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

    suspend fun assertCreatedUser(
        dto: CreateOrUpdateUserDto,
        response: CreatedUserDto
    ) {
        // Проверка HTTP Response
        response.status shouldBe ES_USER_ANONYMOUS.entityStatusName
        response.email shouldBe dto.email
        response.modifiedLogin shouldBe dto.login

        val userValidators: Array<PropertyValidator<User, *>> = arrayOf(
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

        // Проверка всех полей Entity в БД
        verifyModifiedEntity(
            userRepo.findByLogin(dto.login),
            EA_CREATE_OR_UPDATE_USER,
            verifyAllFields = true,
            *userValidators,
        )
    }

    suspend fun updateUserStatus(
        requestBody: UpdateUserStatusDto,
        rm: String = "/users") {
        val response =
            executePost<UpdateUserStatusDto, UpdatedUserDto>(rm, "/updateStatus", requestBody) { expectStatus().isOk }
        assertUpdatedUser(requestBody, response)
    }

    suspend fun assertUpdatedUser(
        dto: UpdateUserStatusDto,
        response: UpdatedUserDto
    ) {
        // Проверка HTTP Response
        response.newStatus shouldBe dto.status
        response.modifiedLogin shouldBe dto.login

        val userValidators: Array<PropertyValidator<User, *>> = arrayOf(
            User::entityStatus verify { it shouldBe EntityStatusEnum.findStatus<EntityStatus>(dto.status, ET_USER) },
            User::userId verify { it shouldBe entityId },
            User::entityId verify { it shouldBe userId },
            User::modifyDate verify { it shouldBeGreaterThan createDate },
        )

        verifyModifiedEntity(
            userRepo.findByLogin(dto.login),
            EA_UPDATE_USER_STATUS,
            verifyAllFields = false,
            *userValidators,
        )
    }

    suspend fun createUser(
        requestBody: CreateOrUpdateUserDto,
        rm: String = "/users") {
        val response =
            executePost<CreateOrUpdateUserDto, CreatedUserDto>(rm, "/createOrUpdate", requestBody) { expectStatus().isOk }
        assertCreatedUser(requestBody, response)
    }

    protected fun createLoginUserDto(login: String, password: String) = LoginUserDto(login, password)

    suspend fun loginUser(
        requestBody: LoginUserDto,
        rm: String = "/security") {
        val response =
            executePost<LoginUserDto, LoginUserResponseDto>(rm, "/login", requestBody) { expectStatus().isOk }
        assertCreatedJwts(requestBody, response)
    }

    protected suspend fun assertCreatedJwts(
        dto: LoginUserDto,
        response: LoginUserResponseDto
    ) {
        response.accessToken shouldNotBeNull {}
        response.refreshToken shouldNotBeNull {}

        // access token
        val accessJwtValidators: Array<PropertyValidator<IssuedJwt, *>> = arrayOf(
            IssuedJwt::jwtId verify { it shouldNotBe null;  it?.apply {  this shouldBeGreaterThan 0L } },
            IssuedJwt::issueDate verify { it shouldNotBe null },
            IssuedJwt::validUntil verify { it shouldNotBe null; it shouldBeGreaterThan issueDate},
            IssuedJwt::jwt verify { it shouldBe response.accessToken },
            IssuedJwt::issuedTo verify { it shouldBe dto.login },
            IssuedJwt::tag verify { it shouldBe null },
            IssuedJwt::isRevoked verify { it shouldBe false },
            IssuedJwt::revokeDate verify { it shouldBe null },
        )

        val accessJwt = verifyModifiedEntity(
            accessJwtRepo.findByJwt(response.accessToken),
            verifyAllFields = true,
            *accessJwtValidators,
        )

        // refresh token
        val refreshJwtValidators: Array<PropertyValidator<RefreshJwt, *>> = arrayOf(
            RefreshJwt::jwtId verify { it shouldNotBe null
                it?.apply {  this shouldBeGreaterThan (accessJwt.jwtId ?: error("accessJwt is null")) }},
            RefreshJwt::parentJwtId verify { it shouldBe accessJwt.jwtId },
            RefreshJwt::issueDate verify { it shouldNotBe null },
            RefreshJwt::validUntil verify { it shouldNotBe null; it shouldBeGreaterThan issueDate},
            RefreshJwt::jwt verify { it shouldBe response.refreshToken },
            RefreshJwt::isRevoked verify { it shouldBe false },
            RefreshJwt::revokeDate verify { it shouldBe null },
        )

        verifyModifiedEntity(
            refreshJwtRepo.findByJwt(response.refreshToken),
            verifyAllFields = true,
            *refreshJwtValidators,
        )
    }
}
