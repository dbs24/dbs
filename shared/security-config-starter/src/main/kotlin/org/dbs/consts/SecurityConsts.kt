package org.dbs.consts

import io.jsonwebtoken.Claims
import org.dbs.consts.SpringCoreConst.DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_ADDITIONAL_PATH
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_CREDENTIALS
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_HEADERS
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_METHODS
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_ALLOWED_PATH
import org.dbs.consts.SpringCoreConst.PropertiesNames.NETWORK_CORS_MAX_AGE
import org.dbs.consts.SysConst.EMPTY_STRING

typealias ClaimsGet = (Jwt) -> Claims

object SecurityConsts {

    const val SERV_JWT_EXPIRATION_TIME = 100500100500L
    const val PASSWORD_ENCODER_STRENGTH_DEF = 9
    const val JWT_MIN_SIZE_DEF = 50

    object Claims {
        const val CL_IP = "IP_ADDRESS"
        const val CL_AUTH_SERVICE = "AUTH_SERVICE"
        const val CL_INTERNAL_SERVICE = "INTERNAL_SERVICE"
        const val CL_USER_AGENT = "USER_AGENT"
    }

    object Cors {
        val CORS_CONFIG_SET =
            mapOf(
                "CORS allowed path" to NETWORK_CORS_ALLOWED_PATH,
                "CORS allowed additional path" to NETWORK_CORS_ALLOWED_ADDITIONAL_PATH,
                "CORS allowed headers" to NETWORK_CORS_ALLOWED_HEADERS,
                "CORS allowed methods" to NETWORK_CORS_ALLOWED_METHODS,
                "CORS allowed credentials" to NETWORK_CORS_ALLOWED_CREDENTIALS,
                "CORS max age" to NETWORK_CORS_MAX_AGE,
                DELIMITER to EMPTY_STRING
            )
    }
}
