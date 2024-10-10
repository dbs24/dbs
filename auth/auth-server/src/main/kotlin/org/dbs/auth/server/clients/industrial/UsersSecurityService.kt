package org.dbs.auth.server.clients.industrial

import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_JWT_KEY
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_TOKEN_KIND
import org.dbs.auth.server.consts.AuthServerConsts.Claims.TK_ACCESS_TOKEN
import org.dbs.auth.server.consts.AuthServerConsts.Claims.TK_REFRESH_TOKEN
import org.dbs.auth.server.consts.AuthServerConsts.SB_JWT_STRING_KEY_LENGTH
import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.enums.ApplicationEnum.CHESS_COMMUNITY
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.AuthServiceLayer.Companion.jwtSecurityService
import org.dbs.auth.server.service.AuthServiceLayer.Companion.jwtStorageService
import org.dbs.consts.*
import org.dbs.consts.SecurityConsts.Claims.CL_IP
import org.dbs.consts.SecurityConsts.Claims.CL_USER_AGENT
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_INDUSTRIAL_ACCESS_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_INDUSTRIAL_REFRESH_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_INDUSTRIAL_SECRET_KEY
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.goods.UsersConsts.Claims.CL_USER_LOGIN
import org.dbs.goods.UsersConsts.Claims.CL_USER_PRIVILEGES
import org.dbs.protobuf.core.JwtsExp
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.ipAddress
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime.now


@Service
class UsersSecurityService(
    @Value("\${$JWT_INDUSTRIAL_SECRET_KEY:ThisIsSecretOf64ByteLength}")
    private val secretKey: String,
    @Value("\${$JWT_INDUSTRIAL_ACCESS_EXPIRATION_TIME:28800}")
    private val userAccessExpirationTime: DateTimeLong,
    @Value("\${$JWT_INDUSTRIAL_REFRESH_EXPIRATION_TIME:57600}")
    private val userRefreshExpirationTime: DateTimeLong,
) : AbstractApplicationService() {

    private val key by lazy { jwtSecurityService.buildKey(secretKey) }
    override fun initialize() = super.initialize().also {
        logger.debug("${javaClass.simpleName}: registry secretKey: '${secretKey.last15()}'")
    }

    private fun createAccessJwtInternal(
        tokenKey: String,
        userAgent: String,
        validUntil: OperDate,
        claimKeyValue: String,
        expirationTime: DateTimeLong,
        ip: String,
        extClaims: MutableMap<String, String>,
    ): Mono<IssuedJwt> =
        jwtStorageService.createAndSaveAccessJwt(
            tokenKey,
            validUntil,
            CHESS_COMMUNITY,
            claimKeyValue
        ) {
            val claims = createMap {
                if (ip != UNKNOWN) it[CL_IP] = ip.ipAddress()
                it[CL_JWT_KEY] = tokenKey
                it[CL_TOKEN_KIND] = TK_ACCESS_TOKEN
                it[CL_USER_AGENT] = userAgent
            }.also { cl -> extClaims.forEach { cl[it.key] = it.value } }
            logger.debug { "$tokenKey: assigned claims: $claims" }
            jwtSecurityService.generateJwt(this.javaClass.packageName, claims, expirationTime, key)
        }

    private fun createRefreshTokenInternal(
        parentJwtId: JwtId,
        tokenKey: String,
        validUntil: OperDate,
        claimKey: String,
        claimKeyValue: String,
        expirationTime: DateTimeLong,
        userAgent: String,
        ip: String,
    ): Mono<RefreshJwt> =
        jwtStorageService.createAndSaveRefreshJwt(
            parentJwtId,
            validUntil,
            tokenKey,
        ) {
            val claims = createMap {
                it[CL_IP] = ip.ipAddress()
                it[claimKey] = claimKeyValue
                it[CL_USER_AGENT] = userAgent
                it[CL_TOKEN_KIND] = TK_REFRESH_TOKEN
            }
            jwtSecurityService.generateJwt(this.javaClass.packageName, claims, expirationTime, key)
        }

    fun createUserJwt(
        userLogin: Login,
        userAgent: String,
        ip: IpAddress,
        privilegeCodes: Collection<PrivilegeCode>,
    ) = createUserJwt(userLogin, userAgent, userAccessExpirationTime, ip, privilegeCodes)

    private fun createUserJwt(
        login: Login,
        userAgent: String,
        expirationTime: DateTimeLong,
        ip: IpAddress,
        privilegeCodes: Collection<PrivilegeCode>,
    ): Mono<IssuedJwt> = StringBuilder(SB_JWT_STRING_KEY_LENGTH).run {

        val validUntil = now().plusSeconds(expirationTime)
        append(login.plus(";"))
        append(ip.plus(";"))

        // final key
        val tokenKey = toString()
        logger.info("create/update access tokenKey for manager '$login': $tokenKey")

        val claims = createMap {
            it[CL_USER_LOGIN] = login
        }.also { cl -> cl[CL_USER_PRIVILEGES] = privilegeCodes.joinToString(separator = ",") { it } }

        createAccessJwtInternal(tokenKey, userAgent, validUntil, login, userAccessExpirationTime, ip, claims)
    }

    fun createUserRefreshJwt(jwtId: JwtId, managerLogin: Login, userAgent: String, ip: IpAddress) =
        createUserRefreshJwt(jwtId, managerLogin, userRefreshExpirationTime, userAgent, ip)

    private fun createUserRefreshJwt(
        jwtId: JwtId,
        managerLogin: Login,
        expirationTime: DateTimeLong,
        userAgent: String,
        ip: IpAddress,
    ): Mono<RefreshJwt> = StringBuilder(SB_JWT_STRING_KEY_LENGTH).run {

        val validUntil = now().plusSeconds(expirationTime)
        append(managerLogin.plus(";"))
        append(ip.plus(";"))

        val tokenKey: String = this.toString()
        logger.debug("refresh token 4 manager: $managerLogin ['$ip']")

        createRefreshTokenInternal(
            jwtId, tokenKey, validUntil,
            CL_USER_LOGIN, managerLogin, expirationTime, userAgent, ip
        )
    }

    fun revokeExistsJwt(managerLogin: Login, application: ApplicationEnum) =
        jwtStorageService.revokeExistsJwt(managerLogin, application)

    fun buildAccessJwtsExp(jwt: Jwt): JwtsExp = JwtsExp.newBuilder()
        .setJwt(jwt)
        .setExpiryAt(now().plusSeconds(userAccessExpirationTime).toLong())
        .build()

    fun buildRefreshJwtsExp(jwt: Jwt): JwtsExp = JwtsExp.newBuilder()
        .setJwt(jwt)
        .setExpiryAt(now().plusSeconds(userRefreshExpirationTime).toLong())
        .build()

}
