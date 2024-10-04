package org.dbs.outOfService.consts

import org.dbs.consts.RestHttpConsts.RouteAction.URI_CREATE_OR_UPDATE
import org.dbs.consts.RestHttpConsts.RouteAction.URI_GET
import org.dbs.consts.RestHttpConsts.RouteVersion.URI_V1
import org.dbs.consts.RestHttpConsts.URI_API

object OutOfServiceConsts {
    object Main {
        const val PROFILE = "config.restful.profile.name"
    }

    object Routes {

        private const val URI_CORE_OUT_OF_SERVICE = "/out-of-service"

        const val ROUTE_CREATE_OR_UPDATE_CORE_OUT_OF_SERVICE =
            URI_API + URI_CORE_OUT_OF_SERVICE + URI_V1 + URI_CREATE_OR_UPDATE
        const val ROUTE_GET_CORE_OUT_OF_SERVICE =
            URI_API + URI_CORE_OUT_OF_SERVICE + URI_V1 + URI_GET

        object Tags {
            const val ROUTE_TAG_CORE_OUT_OF_SERVICE = "CoreOutOfService"
        }
    }
}
