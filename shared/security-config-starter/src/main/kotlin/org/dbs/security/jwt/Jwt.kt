package org.dbs.security.jwt

import io.jsonwebtoken.Claims
import org.dbs.consts.SysConst.UNKNOWN

import org.springframework.web.server.ServerWebExchange

data class Jwt(
    val token: String,
    val claims: Claims,
    val serverWebExchange: ServerWebExchange,
    val isTokenExpired: Boolean = true,
    val requestIp: String = "0.0.0.0",
    val userAgent: String = UNKNOWN
)
