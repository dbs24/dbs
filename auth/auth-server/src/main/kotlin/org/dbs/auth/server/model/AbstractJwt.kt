package org.dbs.auth.server.model

import org.dbs.consts.JwtId
import org.dbs.spring.ref.AbstractRefEntity

sealed class AbstractJwt : AbstractRefEntity<JwtId>()
