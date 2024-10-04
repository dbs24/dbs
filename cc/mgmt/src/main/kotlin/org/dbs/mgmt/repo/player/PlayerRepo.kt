package org.dbs.mgmt.repo.player

import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.dbs.mgmt.model.player.Player as ENTITY

interface PlayerRepo : CoroutineCrudRepository<ENTITY, EntityId> {
    suspend fun findByLogin(login: EntityCode): ENTITY?
    suspend fun findByEmail(email: Email): ENTITY?
}