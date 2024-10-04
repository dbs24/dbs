package org.dbs.chess24.funcs

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
import org.dbs.mgmt.dao.PlayerDao
import org.dbs.mgmt.model.player.Player
import org.dbs.mgmt.service.PlayerService
import org.dbs.player.PlayerCore.EntityTypes.ET_PLAYER
import org.dbs.player.PlayerPassword
import org.dbs.player.PlayersConsts.Routes.ROUTE_CREATE_OR_UPDATE_PLAYER
import org.dbs.player.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_PASSWORD
import org.dbs.player.PlayersConsts.Routes.ROUTE_UPDATE_PLAYER_STATUS
import org.dbs.player.dto.player.*
import org.dbs.ref.serv.enums.CountryEnum.Companion.getRandomCountry
import org.dbs.ref.serv.enums.GenderEnum.Companion.getRandomGender
import org.dbs.test.ko.WebTestClientFuncs.executePostRequestV2
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

object PlayerFuncs : Logging {

    private suspend fun PlayerDao.playerCount() = playerRepo.count()
    suspend fun PlayerService.playerCount() = dao.playerCount()
    suspend fun AbstractChessTest.playerCount() = playerService.playerCount()

    val playerStatusesNames by lazy { ET_PLAYER.existsEntityStatuses.map { it.entityStatusName } }

    suspend fun AbstractChessTest.createTestPlayer() = this.createOrUpdateTestPlayer()

    suspend fun AbstractChessTest.createOrUpdateTestPlayer(
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
    ): Mono<Player> = run {

        runTest {
            val testedRoute = ROUTE_CREATE_OR_UPDATE_PLAYER
            logger.info("execute route '$testedRoute'")

            val dto = CreateOrUpdatePlayerDto(
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
                testedRoute, CreateOrUpdatePlayerRequest(requestBodyDto = dto), CreatePlayerResponse::class,
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

                playerService.findPlayerByLogin(dto.login).toMono()
                    .switchIfEmpty { error("${dto.login}: unknown Player") }
                    .map { createdPlayer ->
                        logger.debug { "compare Player attrs (${dto.login})" }
                        matchDto2Entity(dto, createdPlayer) {
                            with(createdPlayer) {
                                login.shouldBeEqual(this.login)
                                firstName.shouldBeEqual(this.firstName!!)
                                lastName.shouldBeEqual(this.lastName!!)
                                middleName.shouldBeEqual(this.middleName!!)
                                playerService.passwordEncoder.matches(this.password, password)
                                birthDate.shouldBeEqual(this.birthDate!!.toInt())
                                country.shouldBeEqual(this.country!!)
                                gender.shouldBeEqual(this.gender!!)
                                avatar.shouldBeEqual(this.avatar!!)
                                smallAvatar.shouldBeEqual(this.smallAvatar!!)

                            }
                        }
                        createdPlayer
                    }
            }
        }
    }

    suspend fun AbstractChessTest.updatePlayerStatus(
        login: EntityCode = generateTestLogin(),
        newStatus: EntityStatusName
    ): Mono<Player> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_PLAYER_STATUS
            logger.info("execute route '$testedRoute'")

            val dto = UpdatePlayerStatusDto(
                login = login,
                newStatus = newStatus
            )

            executePostRequestV2(
                testedRoute, UpdatePlayerStatusRequest(requestBodyDto = dto), UpdatePlayerStatusResponse::class,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.login.shouldBeEqual(dto.login)
                it.newStatus.shouldBeEqual(dto.newStatus)

                (playerService.findPlayerByLogin(dto.login)
                    ?.also {
                        logger.debug { "compare Player attrs (${dto.login})" }
                        matchDto2Entity(dto, it) {
                            with(it) {
                                login.shouldBeEqual(this.login)
                                newStatus.shouldBeEqual(this.entityCore.entityStatus.entityStatusName)
                            }
                        }
                    } ?: error("${dto.login}: unknown player")).toMono()
            }
        }
    }

    suspend fun AbstractChessTest.updatePlayerPassword(
        login: EntityCode = generateTestLogin(),
        newPassword: PlayerPassword,
        passwordEncoder: PasswordEncoder
    ): Mono<Player> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_PLAYER_PASSWORD
            logger.info("execute route '$testedRoute'")

            val dto = UpdatePlayerPasswordDto(
                login = login,
                newPassword = newPassword
            )

            executePostRequestV2(
                testedRoute, UpdatePlayerPasswordRequest(requestBodyDto = dto), UpdatePlayerPasswordResponse::class,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.login.shouldBeEqual(dto.login)

                (playerService.findPlayerByLogin(dto.login)
                    ?.also {
                        logger.debug { "compare Player attrs (${dto.login})" }
                        matchDto2Entity(dto, it) {
                            with(it) {
                                login.shouldBeEqual(this.login)
                                passwordEncoder.matches(newPassword, this.password)
                            }
                        }
                    } ?: error("${dto.login}: unknown player")).toMono()
            }
        }
    }

}
