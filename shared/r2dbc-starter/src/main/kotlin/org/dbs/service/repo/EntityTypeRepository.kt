package org.dbs.service.repo

import org.dbs.entity.core.EntityType
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EntityTypeRepository : CoroutineCrudRepository<EntityType, Int>
