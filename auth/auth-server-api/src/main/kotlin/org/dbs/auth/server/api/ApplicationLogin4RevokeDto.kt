package org.dbs.auth.server.api

import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.consts.Jwt
import org.dbs.kafka.api.KafkaDocument

data class ApplicationLogin4RevokeDto(
    val login: String,
    val application: ApplicationEnum
) : KafkaDocument

data class RevokedJwtDto(
    val jwt: Jwt,
) : KafkaDocument
