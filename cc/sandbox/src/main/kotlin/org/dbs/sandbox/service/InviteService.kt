package org.dbs.sandbox.service

import kotlinx.coroutines.reactor.awaitSingle
import org.dbs.application.core.service.funcs.StringFuncs.createRandomString
import org.dbs.consts.EntityCode
import org.dbs.consts.IpAddress
import org.dbs.consts.StringNote
import org.dbs.entity.core.EntityActionEnum
import org.dbs.entity.core.EntityStatusEnum
import org.dbs.invite.InviteCore.isClosedInvite
import org.dbs.service.v2.ActionEvent
import org.dbs.service.v2.EntityCoreVal.Companion.generateNewEntityId
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime.now
import org.dbs.sandbox.dao.InviteDao as DAO
import org.dbs.sandbox.model.invite.GameInvite as ENTITY

@Service
class InviteService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val dao: DAO,
    val inviteFactory: InviteFactory,
    val eventPublisher: ApplicationEventPublisher,
) : AbstractApplicationService() {

    suspend fun saveHistory(entity: ENTITY): ENTITY = entity.run {
        if (!justCreated.value)
            r2dbcPersistenceService.saveEntityHistCo(inviteFactory.createHist(entity))
                .let {
                    dao.invalidateCaches(entity.inviteCode)
                    entity
                }
        else this
    }

    suspend fun saveInvite(
        invite: ENTITY,
        actionEnum: EntityActionEnum,
        remoteAddr: IpAddress,
        actionNote: StringNote,
    ): ENTITY = r2dbcPersistenceService.saveEntity(invite).awaitSingle()
        .also {
            eventPublisher.publishEvent(
                ActionEvent(
                    entityId = it.inviteId,
                    entityTypeId = it.entityType.entityTypeId,
                    actionCodeId = actionEnum.actionCodeId,
                    remoteAddr = remoteAddr,
                    actionNote = actionNote,
                )
            )
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
        invite.copy(
            entityStatus = status,
            modifyDate = now(),
            closeDate = if (isClosedInvite(status)) now() else invite.closeDate,
        ).also {
            dao.invalidateCaches(invite.inviteCode)
            it.justCreated.update(invite.justCreated.value)
        }
}
