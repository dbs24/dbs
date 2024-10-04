package org.dbs.outOfService.repo

import org.dbs.consts.EntityId
import org.dbs.outOfService.model.CoreOutOfService
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface CoreOutOfServiceRepo : R2dbcRepository<CoreOutOfService, EntityId> {
}
