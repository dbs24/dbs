package org.dbs.tree.repo.user

import org.dbs.consts.Email
import org.dbs.consts.EntityCode
import org.dbs.consts.EntityId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.dbs.tree.model.user.User as ENTITY

interface UserRepo : CoroutineCrudRepository<ENTITY, EntityId> {
    suspend fun findByLogin(login: EntityCode): ENTITY?
    suspend fun findByEmail(email: Email): ENTITY?
}