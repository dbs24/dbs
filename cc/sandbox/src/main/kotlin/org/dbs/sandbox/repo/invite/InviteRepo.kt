package org.dbs.sandbox.repo.invite

import org.dbs.consts.EntityCode
import org.dbs.consts.EntityId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.dbs.sandbox.model.hist.GameInviteHist as ENTITY_HIST
import org.dbs.sandbox.model.invite.GameInvite as ENTITY

interface InviteRepo : CoroutineCrudRepository<ENTITY, EntityId> {
    suspend fun findByInviteCode(inviteCode: EntityCode): ENTITY?
}

interface InviteRepoHistCo : CoroutineCrudRepository<ENTITY_HIST, EntityId>