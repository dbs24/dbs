package org.dbs.google

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class SiteVerifyResponse(
    val success: Boolean,
    @JsonProperty("challenge_ts")
    val  challengeTs: LocalDateTime?,
    val  hostName: String?,
    @JsonProperty("error-codes")
    val  errorCodes: Collection<String>?
)
