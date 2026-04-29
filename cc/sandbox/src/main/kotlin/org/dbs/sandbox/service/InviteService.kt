package org.dbs.sandbox.service

import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.application.core.service.funcs.StringFuncs.createRandomString
import org.dbs.consts.EntityCode
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.invite.InviteCore.isClosedInvite
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import org.dbs.sandbox.dao.InviteDao as DAO
import org.dbs.sandbox.model.invite.GameInvite as ENTITY

@Service
class InviteService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val dao: DAO,
    val inviteFactory: InviteFactory,
) : AbstractApplicationService() {

    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
        entity.inviteId?.let {
            r2dbcPersistenceService.saveEntityHistCo(inviteFactory.createHist(entity))
                .let {
                    dao.invalidateCaches(entity.inviteCode)
                    entity
                }
            this
        } ?: this
    }

    suspend fun saveInvite(invite: ENTITY): ENTITY = r2dbcPersistenceService.saveEntity(invite).awaitSingle()

    fun generateInviteCode() = createRandomString(50)

    suspend fun createNewInviteCo(): ENTITY =
        inviteFactory.createNewInvite(generateInviteCode()).also {
                logger.debug { "create new invite: ${it.inviteCode}" }
            }

    suspend fun findInviteByCode(inviteCode: EntityCode): ENTITY? =
        dao.findInviteByCode(inviteCode.also { logger.debug { "find invite code: $inviteCode" } })

    fun setInviteNewStatus(invite: ENTITY, status: EntityStatusEnum): ENTITY = invite.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedInvite(status)) now() else invite.closeDate,
        ).also {
            dao.invalidateCaches(invite.inviteCode)
            //it.justCreated.update(invite.justCreated.value)
        }
}
