package org.dbs.goods.funcs

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.LocalDateFuncs.toInt
import org.dbs.application.core.service.funcs.TestFuncs.generateBirthDateDto
import org.dbs.application.core.service.funcs.TestFuncs.generateFirstOrLastName
import org.dbs.application.core.service.funcs.TestFuncs.generateTestEmail
import org.dbs.application.core.service.funcs.TestFuncs.generateTestLogin
import org.dbs.application.core.service.funcs.TestFuncs.generateTestName
import org.dbs.application.core.service.funcs.TestFuncs.generateTestPassword811
import org.dbs.application.core.service.funcs.TestFuncs.generateTestPhone
import org.dbs.consts.*
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.goods.dao.UserDao
import org.dbs.goods.model.user.User
import org.dbs.goods.service.UserService
import org.dbs.goods.UserCore.EntityTypes.ET_USER
import org.dbs.goods.UserPassword
import org.dbs.goods.UsersConsts.Routes.ROUTE_CREATE_OR_UPDATE_USER
import org.dbs.goods.UsersConsts.Routes.ROUTE_UPDATE_USER_PASSWORD
import org.dbs.goods.UsersConsts.Routes.ROUTE_UPDATE_USER_STATUS
import org.dbs.goods.dto.user.*
import org.dbs.ref.serv.enums.CountryEnum.Companion.getRandomCountry
import org.dbs.ref.serv.enums.GenderEnum.Companion.getRandomGender
import org.dbs.test.ko.WebTestClientFuncs.executePostRequestV2
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

object UserFuncs : Logging {

    private suspend fun UserDao.userCount() = userRepo.count()
    suspend fun UserService.userCount() = dao.userCount()
    suspend fun AbstractUserTest.userCount() = userService.userCount()

    val userStatusesNames by lazy { ET_USER.existsEntityStatuses.map { it.entityStatusName } }

    suspend fun AbstractUserTest.createTestUser() = this.createOrUpdateTestUser()

    suspend fun AbstractUserTest.createOrUpdateTestUser(
        login: EntityCode = generateTestLogin(),
        email: Email = generateTestEmail(),
        oldLogin: EntityCodeNull = null,
        oldEmail: EmailNull = null,
        phone: Phone = generateTestPhone(),
        firstName: String = generateFirstOrLastName(),
        middleName: String = generateFirstOrLastName(),
        lastName: String = generateFirstOrLastName(),
        password: String = generateTestPassword811(),
        birthDate: BirthDateDto = generateBirthDateDto(),
        country: CountryIsoCode = getRandomCountry(),
        gender: AnyCode = getRandomGender(),
        avatar: UriPath = generateTestName(),
        smallAvatar: UriPath = generateTestName(),
    ): Mono<User> = run {

        runTest {
            val testedRoute = ROUTE_CREATE_OR_UPDATE_USER
            logger.info("execute route '$testedRoute'")

            val dto = CreateOrUpdateUserDto(
                oldLogin = oldLogin,
                login = login,
                oldEmail = oldEmail,
                email = email,
                phone = phone,
                firstName = firstName,
                lastName = lastName,
                middleName = middleName,
                password = password,
                birthDate = birthDate,
                country = country,
                gender = gender,
                avatar = avatar,
                smallAvatar = smallAvatar
            )

            executePostRequestV2(
                testedRoute, CreateOrUpdateUserRequest(requestBodyDto = dto), CreateUserResponse::class,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.modifiedLogin.shouldBeEqual(dto.login)
                it.email.shouldBeEqual(dto.email)
                //it.status.shouldBeEqual(dto.email)
//                it.name.shouldBeEqual(name)
//                it.questionsAmount.shouldBeEqual(body.split(QT_QUESTION).size - 1)
//                it.answersAmount.shouldBeEqual(body.split(QT_ANS).size - 1)

                userService.findUserByLogin(dto.login).toMono()
                    .switchIfEmpty { error("${dto.login}: unknown User") }
                    .map { createdUser ->
                        logger.debug { "compare User attrs (${dto.login})" }
                        matchDto2Entity(dto, createdUser) {
                            with(createdUser) {
                                login.shouldBeEqual(this.login)
                                firstName.shouldBeEqual(this.firstName!!)
                                lastName.shouldBeEqual(this.lastName!!)
                                userService.passwordEncoder.matches(this.password, password)
                            }
                        }
                        createdUser
                    }
            }
        }
    }

    suspend fun AbstractUserTest.updateUserStatus(
        login: EntityCode = generateTestLogin(),
        newStatus: EntityStatusName
    ): Mono<User> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_USER_STATUS
            logger.info("execute route '$testedRoute'")

            val dto = UpdateUserStatusDto(
                login = login,
                newStatus = newStatus
            )

            executePostRequestV2(
                testedRoute, UpdateUserStatusRequest(requestBodyDto = dto), UpdateUserStatusResponse::class,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.login.shouldBeEqual(dto.login)
                it.newStatus.shouldBeEqual(dto.newStatus)

                (userService.findUserByLogin(dto.login)
                    ?.also {
                        logger.debug { "compare User attrs (${dto.login})" }
                        matchDto2Entity(dto, it) {
                            with(it) {
                                login.shouldBeEqual(this.login)
                                newStatus.shouldBeEqual(this.entityCore.entityStatus.entityStatusName)
                            }
                        }
                    } ?: error("${dto.login}: unknown user")).toMono()
            }
        }
    }

    suspend fun AbstractUserTest.updateUserPassword(
        login: EntityCode = generateTestLogin(),
        newPassword: UserPassword,
        passwordEncoder: PasswordEncoder
    ): Mono<User> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_USER_PASSWORD
            logger.info("execute route '$testedRoute'")

            val dto = UpdateUserPasswordDto(
                login = login,
                newPassword = newPassword
            )

            executePostRequestV2(
                testedRoute, UpdateUserPasswordRequest(requestBodyDto = dto), UpdateUserPasswordResponse::class,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.login.shouldBeEqual(dto.login)

                (userService.findUserByLogin(dto.login)
                    ?.also {
                        logger.debug { "compare User attrs (${dto.login})" }
                        matchDto2Entity(dto, it) {
                            with(it) {
                                login.shouldBeEqual(this.login)
                                passwordEncoder.matches(newPassword, this.password)
                            }
                        }
                    } ?: error("${dto.login}: unknown user")).toMono()
            }
        }
    }

}
