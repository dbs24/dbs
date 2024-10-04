package org.dbs.outOfService.repo

import org.dbs.consts.EntityId
import org.dbs.outOfService.model.CoreOutOfServiceHist
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface CoreOutOfServiceHistRepo : R2dbcRepository<CoreOutOfServiceHist, EntityId> {
}
