package org.dbs.goods.user

import api.TestConst.PLAYER_TEST_AMOUNT
import api.TestConst.REPEATED_KOTEST_AMOUNT
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.TestFuncs.generateTestPassword811
import org.dbs.application.core.service.funcs.TestFuncs.selectFrom
import org.dbs.goods.funcs.AbstractUserTest
import org.dbs.goods.funcs.UserFuncs.createOrUpdateTestUser
import org.dbs.goods.funcs.UserFuncs.createTestUser
import org.dbs.goods.funcs.UserFuncs.userCount
import org.dbs.goods.funcs.UserFuncs.userStatusesNames
import org.dbs.goods.funcs.UserFuncs.updateUserPassword
import org.dbs.goods.funcs.UserFuncs.updateUserStatus
import org.dbs.goods.model.user.User
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Flux.concat
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers.boundedElastic


typealias MonoUser = Mono<User>
class UserTests(
    passwordEncoder: PasswordEncoder,
) : AbstractUserTest({

    val scheduler = boundedElastic()
    val entitiesCount = REPEATED_KOTEST_AMOUNT

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val createUserCaption = "create users  ($entitiesCount)"
    Given(createUserCaption) {

        val users = concat(
            createCollection<MonoUser>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create user: $it" }
                    list.add(createTestUser())
                }
            })
            .collectList()
            .publishOn(scheduler)


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        When(createUserCaption) {
            Then(createUserCaption) {
                users
                    .flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            logger().debug { "try 2 create user: ${it.login}" }
                            createOrUpdateTestUser(
                                it.login,
                                it.email!!,
                                it.login,
                                it.email
                            )
                        }
                    }.count().awaitSingle()
            }

            // validate amount of users
            Then("validate amount of users ($entitiesCount + $PLAYER_TEST_AMOUNT)") {
                userCount().shouldBe(entitiesCount + PLAYER_TEST_AMOUNT)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val updateUserCaption = "update users status ($entitiesCount)"
    Given(updateUserCaption) {

        val users = concat(
            createCollection<MonoUser>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create user 4 update status: $it" }
                    list.add(createTestUser())
                }
            })
            .collectList()
            .publishOn(scheduler)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val updateUserStatus = "update users status ($entitiesCount)"
        When(updateUserStatus) {
            Then(updateUserStatus) {
                users
                    .flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            logger().debug { "try 2 update user status: ${it.login}" }
                            updateUserStatus(
                                it.login,
                                selectFrom(userStatusesNames)
                            )
                        }
                    }.count().awaitSingle()
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        val updateUserPassword = "update users password ($entitiesCount)" // why do we need entitiesCount here
        When(updateUserPassword) {
            Then(updateUserPassword) {
                users
                    .flatMapMany { fromIterable(it) }
                    .flatMap {

                        runBlocking {
                            logger().debug { "try 2 update user password: ${it.login}" }
                            updateUserPassword(
                                it.login,
                                generateTestPassword811(),
                                passwordEncoder
                            )
                        }
                    }.count().awaitSingle()
            }
        }
    }
})
