package org.dbs.spring.security.api

import io.jsonwebtoken.Claims
import org.dbs.consts.Jwt
import org.dbs.spring.core.api.PublicApplicationBean
import javax.crypto.SecretKey

interface JwtSecurityServiceApi: PublicApplicationBean {
    fun buildKey(secretKey: String): SecretKey
    fun getServiceJwt(): Jwt
    fun getBearerServiceJwt(): Jwt
    fun validateJwt(jwt: Jwt): Boolean
    fun isServiceJwt(jwt: Jwt): Boolean
    fun getAllClaimsFromJwt(jwt: Jwt): Claims

    fun getClaim(jwt: Jwt, claimName: String): String?
    fun getClaimExpired(jwt: Jwt, claimName: String): String?
}
