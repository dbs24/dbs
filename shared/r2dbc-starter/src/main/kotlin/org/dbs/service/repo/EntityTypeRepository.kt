package org.dbs.service.repo

import org.dbs.entity.core.EntityType
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface EntityTypeRepository : R2dbcRepository<EntityType, Int>
