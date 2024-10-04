package org.dbs.service.repo

import org.dbs.service.model.PrivilegeGroup
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface PrivilegeGroupRepository : R2dbcRepository<PrivilegeGroup, Int>