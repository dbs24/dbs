package org.dbs.service.repo

import org.dbs.entity.core.ActionCode
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ActionCodeRepository : R2dbcRepository<ActionCode, Int>