package org.dbs.spring.security.api

import io.jsonwebtoken.Claims
import org.dbs.spring.core.api.PublicApplicationBean
import javax.crypto.SecretKey

interface JwtSecurityServiceApi: PublicApplicationBean {
    fun buildKey(secretKey: String): SecretKey
    fun getServiceJwt(): String
    fun getBearerServiceJwt(): String
    fun validateJwt(jwt: String): Boolean
    fun isServiceJwt(jwt: String): Boolean
    fun getAllClaimsFromJwt(jwt: String): Claims

    fun getClaim(jwt: String, claimName: String): String?
    fun getClaimExpired(jwt: String, claimName: String): String?
}
