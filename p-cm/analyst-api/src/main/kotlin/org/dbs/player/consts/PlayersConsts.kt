package org.dbs.player.consts

import org.dbs.consts.AnyCode
import org.dbs.consts.EntityId
import org.dbs.consts.RestHttpConsts.RouteAction.URI_CREATE_OR_UPDATE
import org.dbs.consts.RestHttpConsts.RouteAction.URI_GET
import org.dbs.consts.RestHttpConsts.RouteAction.URI_UPDATE
import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API
import org.dbs.consts.RestHttpConsts.URI_LOGIN
import org.dbs.consts.RestHttpConsts.URI_REFRESH_JWT
import org.dbs.consts.SysConst
import org.dbs.consts.SysConst.APP_CM
import org.dbs.consts.SysConst.SLASH

typealias CustomerLogin = AnyCode
typealias Fen = String
typealias SchoolCustomerId = EntityId
typealias PlayerId = EntityId
typealias SolutionId = EntityId
typealias ManagerIdNull = PlayerId?
typealias PlayerLogin = AnyCode
typealias ManagerStatus = String
typealias ManagerLoginNull = PlayerLogin?
typealias ManagerFirstName = String
typealias ManagerLastName = String
typealias ManagerAvatarImageNull = String?
typealias PasswordId = EntityId

object PlayersConsts {

    object Main {
        const val PROFILE = "config.restful.profile.name"
        const val UPDATE_PASSWORD = true
        const val KEEP_PASSWORD = !UPDATE_PASSWORD
    }

    object Routes {
        const val URI_PLAYER = "/player"
        const val URI_SOLUTION = "/solution"

        private const val URI_STATUS = "/status"
        private const val URI_CREDENTIALS = "/credentials"

        const val ROUTE_CREATE_OR_UPDATE_PLAYER = URI_API + URI_PLAYER + URI_V1 + URI_CREATE_OR_UPDATE
        const val ROUTE_UPDATE_PLAYER_STATUS = URI_API + URI_PLAYER + URI_STATUS + URI_V1 + URI_UPDATE
        const val ROUTE_GET_PLAYER_CREDENTIALS = URI_API + URI_PLAYER + URI_CREDENTIALS + URI_V1 + URI_GET

        private const val CM_PLAYER_ROUTES = URI_API + SLASH + APP_CM + URI_PLAYER
        const val CM_LOGIN_PLAYER = CM_PLAYER_ROUTES + URI_V1 + URI_LOGIN
        const val CM_REFRESH_PLAYER_JWT = CM_PLAYER_ROUTES + URI_V1 + URI_REFRESH_JWT

        private const val CM_SOLUTION_ROUTES = URI_API + URI_SOLUTION
        const val CM_GET_SOLUTION = CM_SOLUTION_ROUTES + URI_V1 + URI_GET

        object Tags {
            const val ROUTE_TAG_SOLUTION = "Solution"
            const val ROUTE_TAG_PLAYER = "Player"
        }
    }

    object Claims {
        const val CL_PLAYER_LOGIN = "PLAYER_LOGIN"
    }

    object CmQueryParams {
        const val QP_PLAYER_LOGIN = "playerLogin"
        const val QP_FEN = "fen"
        const val QP_DEPTH = "depth"
        const val QP_TIMEOUT = "timeout"
        const val QP_WHITE_MOVE = "isWhiteMove"
    }
}
