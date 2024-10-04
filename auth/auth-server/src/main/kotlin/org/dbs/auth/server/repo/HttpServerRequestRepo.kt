package org.dbs.auth.server.repo

import org.dbs.auth.server.model.HttpServerRequest
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface HttpServerRequestRepo : R2dbcRepository<HttpServerRequest, Long?>