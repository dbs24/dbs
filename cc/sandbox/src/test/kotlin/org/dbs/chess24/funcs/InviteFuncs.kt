package org.dbs.chess24.funcs

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.TestFuncs.generateBool
import org.dbs.application.core.service.funcs.TestFuncs.generateTestLogin
import org.dbs.application.core.service.funcs.TestFuncs.generateTestRangeInteger
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityCodeNull
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.SysConst.ACTUAL
import org.dbs.consts.SysConst.STRING_NULL
import org.dbs.entity.core.v2.status.EntityStatusName
import org.dbs.invite.InviteConsts.Routes.ROUTE_CREATE_OR_UPDATE_INVITE
import org.dbs.invite.InviteConsts.Routes.ROUTE_UPDATE_INVITE_STATUS
import org.dbs.invite.InviteCore.EntityTypes.ET_INVITE
import org.dbs.invite.dto.invite.*
import org.dbs.sandbox.dao.InviteDao
import org.dbs.sandbox.model.invite.GameInvite
import org.dbs.sandbox.service.InviteService
import org.dbs.test.ko.WebTestClientFuncs.executePostRequestV2
import org.springframework.http.HttpHeaders.AUTHORIZATION
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

object InviteFuncs : Logging {

    private suspend fun InviteDao.inviteCount() = inviteRepo.count()
    suspend fun InviteService.inviteCount() = dao.inviteCount()
    suspend fun AbstractSandBoxTest.inviteCount() = inviteService.inviteCount()

    val inviteStatusesNames by lazy { ET_INVITE.existsEntityStatuses.map { it.entityStatusName } }

    suspend fun AbstractSandBoxTest.createTestInvite() = this.createOrUpdateTestInvite()

    suspend fun AbstractSandBoxTest.createOrUpdateTestInvite(
        inviteCode: EntityCodeNull = STRING_NULL,
    ): Mono<GameInvite> = run {

        runTest {
            val testedRoute = ROUTE_CREATE_OR_UPDATE_INVITE
            logger.info("execute route '$testedRoute'")

            val dto = CreateOrUpdateInviteDto(
                inviteCode = inviteCode,
                playerLogin = "player${generateTestRangeInteger(1, 1000)}",
                gameType = 0,
                requiredRating = 0,
                validDate = 0,
                whiteSide = generateBool(),
            )

            executePostRequestV2(
                testedRoute, CreateOrUpdateInviteRequest(requestBodyDto = dto), CreateInviteResponse::class,
                { it.add(AUTHORIZATION, BEARER + playerJwt) }
            )
            {
                dto.shouldNotBeNull()

                inviteCode?.apply {
                    this.shouldBeEqual(it.inviteCode)
                }

                it.playerLogin.shouldBeEqual(dto.playerLogin)
                it.status.shouldBeEqual(ACTUAL)
                //it.status.shouldBeEqual(dto.email)

                inviteService.findInviteByCode(it.inviteCode).toMono()
                    .switchIfEmpty { error("${inviteCode}: unknown Invite code") }
                    .map { createdInvite ->
                        logger.debug { "compare Invite attrs (${dto.inviteCode})" }
                        matchDto2Entity(dto, createdInvite) {
                            with(createdInvite) {
                                playerLogin.shouldBeEqual(this.playerLogin)
                                inviteCode?.apply {
                                    this.shouldBeEqual(it.inviteCode)
                                }
                                gameType.shouldBeEqual(this.gameType)
                            }
                        }
                        createdInvite
                    }
            }
        }
    }

    suspend fun AbstractSandBoxTest.updateInviteStatus(
        inviteCode: EntityCode = generateTestLogin(),
        newStatus: EntityStatusName
    ): Mono<GameInvite> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_INVITE_STATUS
            logger.info("execute route '$testedRoute'")

            val dto = UpdateInviteStatusDto(
                inviteCode = inviteCode,
                newStatus = newStatus
            )

            executePostRequestV2(
                testedRoute, UpdateInviteStatusRequest(requestBodyDto = dto), UpdateInviteStatusResponse::class,
                { it.add(AUTHORIZATION, BEARER + playerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.inviteCode.shouldBeEqual(dto.inviteCode)
                it.newStatus.shouldBeEqual(dto.newStatus)

                (inviteService.findInviteByCode(dto.inviteCode)
                    ?.also {
                        logger.debug { "compare Invite attrs (${dto.inviteCode})" }
                        matchDto2Entity(dto, it) {
                            with(it) {
                                inviteCode.shouldBeEqual(this.inviteCode)
                                newStatus.shouldBeEqual(this.entityCore.entityStatus.entityStatusName)
                            }
                        }
                    } ?: error("${dto.inviteCode}: unknown invite")).toMono()
            }
        }
    }
}
