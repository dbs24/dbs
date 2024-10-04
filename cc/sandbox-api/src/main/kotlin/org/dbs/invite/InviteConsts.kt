package org.dbs.invite

import org.dbs.consts.AnyCode
import org.dbs.consts.EntityId
import org.dbs.consts.RestHttpConsts.RouteAction.URI_CREATE_OR_UPDATE
import org.dbs.consts.RestHttpConsts.RouteAction.URI_UPDATE
import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API


typealias InviteId = EntityId
typealias PlayerLogin = AnyCode
object InviteConsts {

    object Names {
        const val INVITE = "Game Invite"
    }


    object Routes {
        const val URI_INVITE = "/invite"

        private const val URI_STATUS = "/status"

        const val ROUTE_CREATE_OR_UPDATE_INVITE = URI_API + URI_INVITE + URI_V1 + URI_CREATE_OR_UPDATE
        const val ROUTE_UPDATE_INVITE_STATUS = URI_API + URI_INVITE + URI_STATUS + URI_V1 + URI_UPDATE

        object Tags {
            const val ROUTE_TAG_INVITE = "Invite"
        }
    }

    object Claims {
        const val CL_INVITE_CODE = "INVITE_CODE"
    }

    object CmQueryParams {
        const val QP_PLAYER_LOGIN = "playerLogin"
    }
}
