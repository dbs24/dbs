package org.dbs.user

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
        const val USER = "User"
        const val PROJECT = "Project"
    }

}
