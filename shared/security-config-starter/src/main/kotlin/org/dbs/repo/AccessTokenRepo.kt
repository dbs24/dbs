package org.dbs.repo

import org.dbs.consts.EntityId
import org.dbs.model.IssuedJwt
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.dbs.model.IssuedJwt as ENTITY

interface AccessJwtRepo : CoroutineCrudRepository<ENTITY, EntityId> {
    suspend fun findByJwt(jwt: String): IssuedJwt?
}