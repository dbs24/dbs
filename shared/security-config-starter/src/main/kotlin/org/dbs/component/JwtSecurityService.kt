package org.dbs.component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.dbs.application.core.service.funcs.GetNetworkAddress.allAddresses
import org.dbs.application.core.service.funcs.GetNetworkAddress.getAddress
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.ClaimsGet
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.RestHttpConsts.URI_IP
import org.dbs.consts.RestHttpConsts.isLocalAddress
import org.dbs.consts.SecurityConsts.Claims.CL_INTERNAL_SERVICE
import org.dbs.consts.SecurityConsts.Claims.CL_IP
import org.dbs.consts.SecurityConsts.Claims.CL_USER_AGENT
import org.dbs.consts.SecurityConsts.JWT_MIN_SIZE_DEF
import org.dbs.consts.SecurityConsts.SERV_JWT_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_SECURITY_SSS_JWT_SECRET_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_SECURITY_SSS_JWT_SECRET_KEY
import org.dbs.consts.StringMap
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.MILLIS_1000
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.rest.api.consts.RestApiConst.Headers.X_REAL_IP
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV4Regex
import org.dbs.rest.service.ServerWebExchangeExt.log
import org.dbs.security.jwt.Jwt
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.security.api.JwtSecurityServiceApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtSecurityService : AbstractApplicationService(), JwtSecurityServiceApi {
    @Value("\${$CONFIG_RESTFUL_SECURITY_SSS_JWT_SECRET_KEY:$VALUE_RESTFUL_SECURITY_SSS_JWT_SECRET_KEY}")
    private val secretKey = EMPTY_STRING
    private val key by lazy { buildKey(secretKey) }
    private val internalServiceJwt by lazy { buildInternalServiceJwt() }

    override fun buildKey(secretKey: String): SecretKey =
        Keys.hmacShaKeyFor(secretKey.toByteArray())

    private val claims: ClaimsGet = { jwt ->
        require(jwt.isNotBlank()) { "Jwt must not be empty" }
        Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).payload
    }

    override fun initialize() {
        super<AbstractApplicationService>.initialize()
        logger.debug("${javaClass.simpleName}: registry secretKey: '${secretKey.last15()}'")
        logger.debug("${javaClass.simpleName}: all addresses: $allAddresses")
    }

    override fun getServiceJwt(): org.dbs.consts.Jwt = internalServiceJwt

    override fun getBearerServiceJwt(): org.dbs.consts.Jwt = BEARER + internalServiceJwt

    fun buildInternalServiceJwt(): String = createMap<String, String>().run {
        this[CL_INTERNAL_SERVICE] = CL_INTERNAL_SERVICE
        getAddress(URI_IP)?.also {
            if (!isLocalAddress.test(it)) {
                logger.debug { "buildInternalServiceJwt: use legal IP ($it)" }
                this[CL_IP] = it
            }
        }
        generateJwt(
            "Internal Jwt",
            this,
            SERV_JWT_EXPIRATION_TIME,
            key
        ).also {
            logger.debug { "build internal security jwt '${it.last15()}'" }
        }
    }

    private fun buildJwt(subject: String, claims: StringMap, expirationTime: Long, key: SecretKey) = run {
        val createdDate = Date()
        val expirationDate = Date(createdDate.time + expirationTime * MILLIS_1000)
        Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(createdDate)
            .expiration(expirationDate)
            .signWith(key)
            .compact()
    }

    fun generateJwt(subject: String, claims: StringMap, expirationTime: Long, key: SecretKey): String =
        buildJwt(subject, claims, expirationTime, key)

    fun generateJwt(subject: String, claims: StringMap, expirationTime: Long): String =
        buildJwt(subject, claims, expirationTime, key)

    override fun getAllClaimsFromJwt(jwt: org.dbs.consts.Jwt): Claims = claims(jwt)

    fun getAllClaimsFromExpiredToken(jwt: org.dbs.consts.Jwt): Claims =
        runCatching {
            claims(jwt)
        }.getOrElse {
            (it as ExpiredJwtException).claims
        }

    private fun getExpirationDateFromToken(jwt: String): Date = getAllClaimsFromJwt(jwt).expiration

    private fun isTokenExpired(jwt: String) = getExpirationDateFromToken(jwt).before(Date())

    override fun validateJwt(jwt: String) = !isTokenExpired(jwt)

    override fun isServiceJwt(jwt: String) = getClaim(jwt, CL_INTERNAL_SERVICE)?.let { true } ?: false

    fun checkToken(jwt: Jwt): Jwt? = jwt.takeUnless { jwt.isTokenExpired }

    fun checkIp(jwt: Jwt): Jwt? = jwt.run {
        this.takeIf { isLocalAddress.test(requestIp) }
            ?: also {
                if (((claims[CL_IP] ?: requestIp) != requestIp)) {
                    logger.warn {
                        "${jwt.token.last15()}: Unauthorized access detected from '${requestIp}' " +
                                "(valid ip is '${claims[CL_IP]}')"
                    }
                }
            }
    }

    fun checkUserAgent(jwt: Jwt): Jwt? = jwt.also {
        if ((it.claims[CL_USER_AGENT] ?: it.userAgent) != it.userAgent) {
            logger.warn {
                "${jwt.token.last15()}: Unauthorized access detected from user agent '${it.userAgent} (${it.requestIp})' " +
                        "(valid user agent is '${it.claims[CL_USER_AGENT]}')"
            }
        }
    }

    fun extractJwt(serverWebExchange: ServerWebExchange): Jwt? =
        serverWebExchange.request.headers.getFirst(AUTHORIZATION)?.let {
            val jwtToken = it.getJwtFromBearer()
            val actualIp = serverWebExchange.request.headers.getFirst(X_REAL_IP)?.replace(allowedIpV4Regex, EMPTY_STRING)
                ?: serverWebExchange.request.remoteAddress?.address?.hostAddress ?: UNKNOWN
            val userAgent = "fff"

            require(jwtToken.length > JWT_MIN_SIZE_DEF)
            { "extractJwt: invalid jwt - '$jwtToken', " + serverWebExchange.log() }

            val claims = getAllClaimsFromJwt(jwtToken)
            Jwt(
                jwtToken,
                claims,
                serverWebExchange,
                claims.expiration.before(Date()),
                actualIp,
                userAgent
            )
        }

    override fun getClaim(jwt: org.dbs.consts.Jwt, claimName: String) = jwt.let {
        require(it.length > JWT_MIN_SIZE_DEF) { "$claimName: invalid jwt - '$jwt'" }
        getAllClaimsFromJwt(it)[claimName] as String?
    }

    override fun getClaimExpired(jwt: org.dbs.consts.Jwt, claimName: String) = jwt.let {
        require(it.length > JWT_MIN_SIZE_DEF) { "$claimName: invalid jwt - '$jwt'" }
        getAllClaimsFromExpiredToken(it)[claimName] as String?
    }
}
