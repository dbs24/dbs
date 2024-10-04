package org.dbs.rest.api.enums

import org.dbs.rest.api.enums.RestOperCodeEnum.OC_EMPTY_RESPONSE_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_EXPIRED_TOKEN_NOT_SPECIFIED
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_ILLEGAL_REFRESH_TOKEN
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_JWT_WAS_REVOKED
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_PASSWORD_WAS_EXPIRED
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_REFRESH_TOKEN_NOT_SPECIFIED
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNEXPECTED_EXCEPTION
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_ERROR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_EXPIRED_TOKEN
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_REFRESH_TOKEN
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_ACCEPTABLE
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED

enum class RestOperCode2HttpEnum(private val rocList: Collection<RestOperCodeEnum>, val httpCode: HttpStatus) {
    MAP_401(
        setOf(
            OC_PASSWORD_WAS_EXPIRED,
            OC_EXPIRED_TOKEN_NOT_SPECIFIED,
            OC_REFRESH_TOKEN_NOT_SPECIFIED,
            OC_UNKNOWN_EXPIRED_TOKEN,
            OC_UNKNOWN_REFRESH_TOKEN,
            OC_ILLEGAL_REFRESH_TOKEN,
            OC_JWT_WAS_REVOKED
        ), UNAUTHORIZED
    ),
    MAP_500(setOf(OC_UNKNOWN_ERROR, OC_UNEXPECTED_EXCEPTION, OC_EMPTY_RESPONSE_ERROR), INTERNAL_SERVER_ERROR);

    companion object {
        fun findActualHttpCode(
            roc: RestOperCodeEnum,
            hasErrors: Boolean,
            defaultHttpCode: HttpStatus = OK,
        ): HttpStatus =
            entries.firstOrNull { it.rocList.contains(roc) }?.httpCode
                ?: defaultHttpCode.takeUnless { hasErrors } ?: NOT_ACCEPTABLE
    }
}
