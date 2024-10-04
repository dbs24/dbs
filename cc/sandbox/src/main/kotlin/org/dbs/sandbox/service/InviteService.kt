package org.dbs.sandbox.service

import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.application.core.service.funcs.StringFuncs.createRandomString
import org.dbs.consts.EntityCode
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.invite.InviteCore.isClosedInvite
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.service.v2.EntityCoreValExt.updateStatus
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import org.dbs.sandbox.dao.InviteDao as DAO
import org.dbs.sandbox.model.invite.GameInvite as ENTITY

@Service
class InviteService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val dao: DAO,
    val inviteFactory: InviteFactory,
) : AbstractApplicationService() {

    //------------------------------------------------------------------------------------------------------------------
    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
        if (!justCreated.value)
            r2dbcPersistenceService.saveEntityHistCo(inviteFactory.createHist(entity))
                .let {
                    dao.invalidateCaches(entity.inviteCode)
                    entity
                }
        else this
    }

    fun generateInviteCode() = createRandomString(50)

    suspend fun createNewInviteCo(): ENTITY =
        generateNewEntityId().toMono()
            .map { newEntityId ->
                val newInviteCode = generateInviteCode()
                logger.debug { "create new invite: $newInviteCode (entityId=$newEntityId)" }
                inviteFactory.createNewInvite(newEntityId, newInviteCode)
            }.awaitSingle()

    suspend fun findInviteByCode(inviteCode: EntityCode): ENTITY? =
        dao.findInviteByCode(inviteCode.also { logger.debug { "find invite code: $inviteCode" } })

    fun setInviteNewStatus(invite: ENTITY, status: EntityStatusEnum): ENTITY =
        invite.run {
            dao.invalidateCaches(invite.inviteCode)
            updateStatus(status, isClosedInvite(status))
            this
        }
}
