package org.dbs.auth.verify.clients.auth.api

import org.dbs.consts.OperDate

data class JwtAttrs(
    val validUntil: OperDate
)
