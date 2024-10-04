package org.dbs.auth.server.repo

import org.dbs.auth.server.model.Application
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface ApplicationRepo : R2dbcRepository<Application, Int>