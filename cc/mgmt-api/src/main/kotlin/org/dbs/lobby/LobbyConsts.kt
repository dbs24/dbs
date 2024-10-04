package org.dbs.lobby

import org.dbs.consts.AnyCode
import org.dbs.consts.EntityId
import org.dbs.consts.EntityKindId
import org.dbs.consts.EntityName
import org.dbs.consts.RestHttpConsts.RouteAction.URI_CREATE_OR_UPDATE
import org.dbs.consts.RestHttpConsts.RouteAction.URI_UPDATE
import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API

typealias LobbyId = EntityId
typealias LobbyKind = EntityKindId
typealias LobbyCode = AnyCode
typealias LobbyName = EntityName
object LobbyConsts {

    object Names {
        const val LOBBY = "Lobby"
    }

    object Routes {
        const val URI_LOBBY = "/lobby"

        private const val URI_STATUS = "/status"

        const val ROUTE_CREATE_OR_UPDATE_LOBBY = URI_API + URI_LOBBY + URI_V1 + URI_CREATE_OR_UPDATE
        const val ROUTE_UPDATE_LOBBY_STATUS = URI_API + URI_LOBBY + URI_STATUS + URI_V1 + URI_UPDATE


        object Tags {
            const val ROUTE_TAG_LOBBY = "Lobby"
        }
    }
}
