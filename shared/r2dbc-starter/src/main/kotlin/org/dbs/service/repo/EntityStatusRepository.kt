package org.dbs.service.repo

import org.dbs.entity.core.EntityStatus
import org.dbs.entity.core.v2.status.EntityStatusId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface EntityStatusRepository : CoroutineCrudRepository<EntityStatus, EntityStatusId>
