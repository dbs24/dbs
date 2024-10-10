package org.dbs.auth.server.consts

import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API
import org.dbs.consts.RestHttpConsts.URI_LOGIN
import org.dbs.consts.RestHttpConsts.URI_REFRESH_JWT
import org.dbs.consts.RestHttpConsts.URI_VERIFY


object AuthServerConsts {

    const val SB_JWT_STRING_KEY_LENGTH = 128

    object V1 {
        const val ROUTE_USER_V1_SIGN = "/api/auth/sign-in"

        // Query Params


    }

    // Caches
    object URI {
        private const val URI_PLAYER = "/player"
        const val ROUTE_PLAYER_LOGIN = URI_API + URI_PLAYER + URI_V1 + URI_LOGIN
        const val ROUTE_PLAYER_REFRESH_JWT = URI_API + URI_PLAYER + URI_V1 + URI_REFRESH_JWT

        private const val URI_USER = "/user"
        const val ROUTE_USER_LOGIN = URI_API + URI_USER + URI_V1 + URI_LOGIN
        const val ROUTE_USER_REFRESH_JWT = URI_API + URI_USER + URI_V1 + URI_REFRESH_JWT

        private const val URI_JWT = "/jwt"
        const val ROUTE_JWT_VERIFY = URI_API + URI_JWT + URI_V1 + URI_VERIFY
    }

    object YmlKeys {
    }

    object Claims {
        const val CL_JWT_KEY = "JWT_KEY"
        const val CL_TOKEN_KIND = "TOKEN_KIND"
        const val TK_ACCESS_TOKEN = "ACCESS_TOKEN"
        const val TK_REFRESH_TOKEN = "REFRESH_TOKEN"
        const val CL_USER_AGENT = "USER_AGENT"

    }
}
