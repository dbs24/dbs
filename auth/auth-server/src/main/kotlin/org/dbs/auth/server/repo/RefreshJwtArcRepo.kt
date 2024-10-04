package org.dbs.auth.server.repo

import org.dbs.auth.server.model.RefreshJwtArc
import org.dbs.consts.JwtId
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface RefreshJwtArcRepo : R2dbcRepository<RefreshJwtArc, JwtId>