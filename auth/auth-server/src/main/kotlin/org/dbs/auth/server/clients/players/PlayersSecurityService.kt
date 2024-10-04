package org.dbs.auth.server.clients.players

import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_JWT_KEY
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_TOKEN_KIND
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_USER_AGENT
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
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_CC_PLAYER_ACCESS_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_CC_PLAYER_REFRESH_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_CC_SECRET_KEY
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.player.PlayerLogin
import org.dbs.player.PlayersConsts.Claims.CL_PLAYER_LOGIN
import org.dbs.player.PlayersConsts.Claims.CL_PLAYER_PRIVILEGES
import org.dbs.protobuf.core.JwtsExp
import org.dbs.rest.api.ext.AbstractWebClientServiceExt.ipAddress
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime.now


@Service
class PlayersSecurityService(
    @Value("\${$JWT_CC_SECRET_KEY:ThisIsSecretOf64ByteLength}")
    private val secretKey: String,
    @Value("\${$JWT_CC_PLAYER_ACCESS_EXPIRATION_TIME:28800}")
    private val playerAccessExpirationTime: DateTimeLong,
    @Value("\${$JWT_CC_PLAYER_REFRESH_EXPIRATION_TIME:57600}")
    private val playerRefreshExpirationTime: DateTimeLong,
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

    fun createPlayerJwt(
        playerLogin: PlayerLogin,
        userAgent: String,
        ip: IpAddress,
        privilegeCodes: Collection<PrivilegeCode>,
    ) = createPlayerJwt(playerLogin, userAgent, playerAccessExpirationTime, ip, privilegeCodes)

    private fun createPlayerJwt(
        login: PlayerLogin,
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
            it[CL_PLAYER_LOGIN] = login
        }.also { cl -> cl[CL_PLAYER_PRIVILEGES] = privilegeCodes.joinToString(separator = ",") { it } }

        createAccessJwtInternal(tokenKey, userAgent, validUntil, login, playerAccessExpirationTime, ip, claims)
    }

    fun createPlayerRefreshJwt(jwtId: JwtId, managerLogin: PlayerLogin, userAgent: String, ip: IpAddress) =
        createPlayerRefreshJwt(jwtId, managerLogin, playerRefreshExpirationTime, userAgent, ip)

    private fun createPlayerRefreshJwt(
        jwtId: JwtId,
        managerLogin: PlayerLogin,
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
            CL_PLAYER_LOGIN, managerLogin, expirationTime, userAgent, ip
        )
    }

    fun revokeExistsJwt(managerLogin: PlayerLogin, application: ApplicationEnum) =
        jwtStorageService.revokeExistsJwt(managerLogin, application)

    fun buildAccessJwtsExp(jwt: Jwt): JwtsExp = JwtsExp.newBuilder()
        .setJwt(jwt)
        .setExpiryAt(now().plusSeconds(playerAccessExpirationTime).toLong())
        .build()

    fun buildRefreshJwtsExp(jwt: Jwt): JwtsExp = JwtsExp.newBuilder()
        .setJwt(jwt)
        .setExpiryAt(now().plusSeconds(playerRefreshExpirationTime).toLong())
        .build()

}
