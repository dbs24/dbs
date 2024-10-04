package org.dbs.player

import org.dbs.consts.AnyCode
import org.dbs.consts.EntityId
import org.dbs.consts.RestHttpConsts
import org.dbs.consts.RestHttpConsts.RouteAction.URI_CREATE_OR_UPDATE
import org.dbs.consts.RestHttpConsts.RouteAction.URI_GET
import org.dbs.consts.RestHttpConsts.RouteAction.URI_UPDATE
import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API


typealias PlayerId = EntityId
typealias PlayerLogin = AnyCode
typealias PlayerPassword = String
object PlayersConsts {

    object Names {
        const val PLAYER = "Chess Player"
    }


    object Routes {
        const val URI_PLAYER = "/player"

        private const val URI_STATUS = "/status"
        private const val URI_CREDENTIALS = "/credentials"
        private const val URI_PASSWORD = "/password"

        const val ROUTE_CREATE_OR_UPDATE_PLAYER = URI_API + URI_PLAYER + URI_V1 + URI_CREATE_OR_UPDATE
        const val ROUTE_UPDATE_PLAYER_STATUS = URI_API + URI_PLAYER + URI_STATUS + URI_V1 + URI_UPDATE
        const val ROUTE_GET_PLAYER_CREDENTIALS = URI_API + URI_PLAYER + URI_CREDENTIALS + URI_V1 + URI_GET
        const val ROUTE_UPDATE_PLAYER_PASSWORD = URI_API + URI_PLAYER + URI_PASSWORD + URI_V1 + URI_UPDATE

        object Tags {
            const val ROUTE_TAG_PLAYER = "Player"
        }
    }

    object Claims {
        const val CL_PLAYER_LOGIN = "PLAYER_LOGIN"
        const val CL_PLAYER_PRIVILEGES = "PLAYER_PRIVILEGES"
    }

    object CmQueryParams {
        const val QP_PLAYER_LOGIN = "playerLogin"
    }
}
