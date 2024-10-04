package org.dbs.auth.server.repo

import org.dbs.auth.server.model.IssuedJwtArc
import org.dbs.consts.JwtId
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface IssuedJwtArcRepo : R2dbcRepository<IssuedJwtArc, JwtId>