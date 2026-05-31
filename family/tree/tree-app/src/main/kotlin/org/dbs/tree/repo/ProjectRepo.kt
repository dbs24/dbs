package org.dbs.tree.repo

import org.dbs.consts.EntityId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.dbs.tree.model.project.Project as ENTITY

interface ProjectRepo : CoroutineCrudRepository<ENTITY, EntityId> {
    suspend fun findByShortName(shortName: String): ENTITY?
    suspend fun findByOwnerId(owner: EntityId): ENTITY?
}