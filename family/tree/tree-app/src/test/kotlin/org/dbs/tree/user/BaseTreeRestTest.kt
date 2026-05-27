package org.dbs.tree.user

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.consts.PasswordNull
import org.dbs.test.ko.BaseRestSpec
import org.dbs.tree.model.user.User
import org.dbs.tree.repo.user.UserRepo
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ANONYMOUS
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_CREATE_OR_UPDATE_USER
import org.dbs.user.dto.user.CreateOrUpdateUserDto
import org.dbs.user.dto.user.CreatedUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

abstract class BaseTreeRestTest: BaseRestSpec() {

    @Autowired lateinit var userRepo: UserRepo
    @Autowired lateinit var passwordEncoder: PasswordEncoder

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

    protected suspend fun assertCreatedUser(
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

}