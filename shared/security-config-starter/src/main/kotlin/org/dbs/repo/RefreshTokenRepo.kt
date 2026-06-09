package org.dbs.repo

import org.dbs.consts.EntityId
import org.dbs.model.RefreshJwt
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.dbs.model.RefreshJwt as ENTITY

interface RefreshJwtRepo : CoroutineCrudRepository<ENTITY, EntityId> {
    suspend fun findByJwt(jwt: String): RefreshJwt?
}
