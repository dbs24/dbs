package org.dbs.service.repo

import org.dbs.service.model.Privilege
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface PrivilegeRepository : R2dbcRepository<Privilege, Int>