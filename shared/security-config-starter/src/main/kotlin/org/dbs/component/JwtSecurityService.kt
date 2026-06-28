package org.dbs.component

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.dbs.application.core.service.funcs.GetNetworkAddress.allAddresses
import org.dbs.application.core.service.funcs.GetNetworkAddress.getAddress
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.getJwtFromBearer
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.ClaimsGet
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.RestHttpConsts.URI_IP
import org.dbs.consts.RestHttpConsts.isLocalAddress
import org.dbs.consts.SQL_CREATE_ACCESS_TOKEN_TABLES
import org.dbs.consts.SecurityConsts.Claims.CL_ACCESS_TOKEN
import org.dbs.consts.SecurityConsts.Claims.CL_INTERNAL_SERVICE
import org.dbs.consts.SecurityConsts.Claims.CL_IP
import org.dbs.consts.SecurityConsts.Claims.CL_REFRESH_TOKEN
import org.dbs.consts.SecurityConsts.Claims.CL_USER
import org.dbs.consts.SecurityConsts.Claims.CL_USER_AGENT
import org.dbs.consts.SecurityConsts.JWT_MIN_SIZE_DEF
import org.dbs.consts.SecurityConsts.SERV_JWT_EXPIRATION_TIME
import org.dbs.consts.StringMap
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.MILLIS_1000
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.dto.jwt.LoginUserResponseDto
import org.dbs.model.IssuedJwt
import org.dbs.model.RefreshJwt
import org.dbs.model.domain.LoginUserCommand
import org.dbs.model.domain.RefreshTokensCommand
import org.dbs.repo.AccessJwtRepo
import org.dbs.repo.RefreshJwtRepo
import org.dbs.rest.api.consts.RestApiConst.Headers.X_REAL_IP
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV4Regex
import org.dbs.rest.service.ServerWebExchangeExt.log
import org.dbs.rest.validation.ValidateDto
import org.dbs.security.jwt.Jwt
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.spring.security.api.JwtSecurityServiceApi
import org.springframework.beans.factory.SmartInitializingSingleton
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ServerWebExchange
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@ConfigurationProperties("application.jwt")
data class JwtProperties(
    val secretKey: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long
)

@Service
class JwtSecurityService(
    private val accessJwtRepo: AccessJwtRepo,
    private val refreshJwtRepo: RefreshJwtRepo,
    private val jwtProperties: JwtProperties,
    private val databaseClient: DatabaseClient
) : AbstractApplicationService(), JwtSecurityServiceApi, SmartInitializingSingleton {

    private val key by lazy { buildKey(jwtProperties.secretKey) }
    private val internalServiceJwt by lazy { buildInternalServiceJwt() }

    init {
        with(jwtProperties) {
            require(secretKey.isNotEmpty()) { "Access token expiration not defined" }
            require(accessTokenExpiration > 0) { "Access token expiration not defined" }
            require(refreshTokenExpiration > 0) { "Refresh token expiration not defined" }
        }
    }

    private suspend fun createAccessTokens(login: String, userAgent: String, now: LocalDateTime = now()): LoginUserResponseDto {
        val accessExpTime = jwtProperties.accessTokenExpiration
        val refreshExpTime = jwtProperties.refreshTokenExpiration

        val accessToken = createMap<String, String>().run {
            this[CL_USER] = login
            this[CL_USER_AGENT] = userAgent.hashCode().toString()
            generateJwt(
                CL_ACCESS_TOKEN,
                this,
                now,
                accessExpTime,
                key
            ).also {
                logger.debug { "build access security jwt '${it.last15()}'" }
            }
        }

        val refreshToken = createMap<String, String>().run {
            this[CL_USER] = login
            this[CL_USER_AGENT] = userAgent.hashCode().toString()
            generateJwt(
                CL_REFRESH_TOKEN,
                this,
                now,
                refreshExpTime,
                key
            ).also {
                logger.debug { "build refresh security jwt '${it.last15()}'" }
            }
        }

        val accessUntil = now.plusSeconds(accessExpTime)
        val refreshUntil = now.plusSeconds(refreshExpTime)

        val issuedJwt = accessJwtRepo.save(
            IssuedJwt(
                issueDate = now,
                validUntil = accessUntil,
                jwt = accessToken,
                issuedTo = login,
                isRevoked = false,
            )
        )

        refreshJwtRepo.save(
            RefreshJwt(
                issueDate = now,
                jwt = refreshToken,
                parentJwtId = issuedJwt.jwtId ?: error("parent jwt is null"),
                validUntil = refreshUntil,
                isRevoked = false
            )
        )

        return LoginUserResponseDto(accessToken, accessUntil.toLong(), refreshToken, refreshUntil.toLong())

    }

    @ValidateDto
    @Transactional
    suspend fun loginUser(request: LoginUserCommand): LoginUserResponseDto {
        return createAccessTokens(request.login, request.userAgent)
    }

    @ValidateDto
    @Transactional
    suspend fun refresh(request: RefreshTokensCommand): LoginUserResponseDto {
        // cancel replaced token
        val now = now()

        accessJwtRepo.save(request.issuedJwt.copy(revokeDate = now, isRevoked = true))
        refreshJwtRepo.save(request.refreshJwt.copy(revokeDate = now, isRevoked = true))

        val diffInSeconds: Long = ChronoUnit.SECONDS.between(request.issuedJwt.issueDate, now)
        val diffSecs: Long = if (diffInSeconds < 1) 1 else 0
        return createAccessTokens(request.login, request.userAgent, now.plusSeconds(diffSecs))
    }

    override fun buildKey(secretKey: String): SecretKey =
        Keys.hmacShaKeyFor(secretKey.toByteArray())

    private val claims: ClaimsGet = { jwt ->
        require(jwt.isNotBlank()) { "Jwt must not be empty" }
        Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).payload
    }

    override fun initialize() {
        super<AbstractApplicationService>.initialize()
        logger.debug("${javaClass.simpleName}: registry secretKey: '${jwtProperties.secretKey.last15()}'")
        logger.debug("${javaClass.simpleName}: all addresses: $allAddresses")
    }

    override fun getServiceJwt(): String = internalServiceJwt

    override fun getBearerServiceJwt(): String = BEARER + internalServiceJwt

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
            now(),
            SERV_JWT_EXPIRATION_TIME,
            key
        ).also {
            logger.debug { "build internal security jwt '${it.last15()}'" }
        }
    }

    private fun buildJwt(subject: String, claims: StringMap, now: LocalDateTime, expirationTime: Long, key: SecretKey) =
        run {
            val createdDate: Date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant())
            val expirationDate = Date(createdDate.time + expirationTime * MILLIS_1000)
            Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(createdDate)
                .expiration(expirationDate)
                .signWith(key)
                .compact()
        }

    fun generateJwt(
        subject: String,
        claims: StringMap,
        now: LocalDateTime,
        expirationTime: Long,
        key: SecretKey
    ): String =
        buildJwt(subject, claims, now, expirationTime, key)

    fun generateJwt(subject: String, claims: StringMap, now: LocalDateTime, expirationTime: Long): String =
        buildJwt(subject, claims, now, expirationTime, key)

    override fun getAllClaimsFromJwt(jwt: String): Claims = claims(jwt)

    fun getAllClaimsFromExpiredToken(jwt: String): Claims =
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
        require (it.claims[CL_USER_AGENT].toString().toInt() == it.userAgent.hashCode()) {
          "${jwt.token.last15()}: Unauthorized access detected, invalid user agent specified " +
                  "(${it.userAgent.hashCode()}!=${it.claims[CL_USER_AGENT]}) (${it.requestIp}) "
        }
    }

    fun extractJwt(serverWebExchange: ServerWebExchange): Jwt? =
        serverWebExchange.request.headers.getFirst(AUTHORIZATION)?.let {
            val jwtToken = it.getJwtFromBearer()
            val actualIp =
                serverWebExchange.request.headers.getFirst(X_REAL_IP)?.replace(allowedIpV4Regex, EMPTY_STRING)
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

    override fun getClaim(jwt: String, claimName: String) = jwt.let {
        require(it.length > JWT_MIN_SIZE_DEF) { "$claimName: invalid jwt - '$jwt'" }
        getAllClaimsFromJwt(it)[claimName] as String?
    }

    override fun getClaimExpired(jwt: String, claimName: String) = jwt.let {
        require(it.length > JWT_MIN_SIZE_DEF) { "$claimName: invalid jwt - '$jwt'" }
        getAllClaimsFromExpiredToken(it)[claimName] as String?
    }

    override fun afterSingletonsInstantiated() {
        databaseClient.sql(SQL_CREATE_ACCESS_TOKEN_TABLES)
            .then()
            .subscribe()
    }

}
