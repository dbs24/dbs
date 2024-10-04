package org.dbs.service.repo

import org.dbs.entity.core.EntityStatus
import org.dbs.entity.core.v2.status.EntityStatusId
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface EntityStatusRepository : R2dbcRepository<EntityStatus, EntityStatusId> {

}
