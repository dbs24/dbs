package org.dbs.customer

import org.dbs.consts.AnyCode
import org.dbs.consts.EntityId
import org.dbs.consts.RestHttpConsts.RouteAction.URI_CREATE_OR_UPDATE
import org.dbs.consts.RestHttpConsts.RouteAction.URI_GET
import org.dbs.consts.RestHttpConsts.RouteAction.URI_UPDATE
import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API


typealias UserId = EntityId
typealias UserLogin = AnyCode
typealias UserPassword = String
object UsersConsts {

    object Names {
        const val USER = "TikTok User"
    }

    object Routes {
        const val URI_USER = "/user"

        private const val URI_STATUS = "/status"
        private const val URI_CREDENTIALS = "/credentials"
        private const val URI_PASSWORD = "/password"

        const val ROUTE_CREATE_OR_UPDATE_USER = URI_API + URI_USER + URI_V1 + URI_CREATE_OR_UPDATE
        const val ROUTE_UPDATE_USER_STATUS = URI_API + URI_USER + URI_STATUS + URI_V1 + URI_UPDATE
        const val ROUTE_GET_USER_CREDENTIALS = URI_API + URI_USER + URI_CREDENTIALS + URI_V1 + URI_GET
        const val ROUTE_UPDATE_USER_PASSWORD = URI_API + URI_USER + URI_PASSWORD + URI_V1 + URI_UPDATE

        object Tags {
            const val ROUTE_TAG_USER = "User"
        }
    }

    object Claims {
        const val CL_USER_LOGIN = "USER_LOGIN"
        const val CL_USER_PRIVILEGES = "USER_PRIVILEGES"
    }

    object CmQueryParams {
        const val QP_USER_LOGIN = "userLogin"
    }
}
