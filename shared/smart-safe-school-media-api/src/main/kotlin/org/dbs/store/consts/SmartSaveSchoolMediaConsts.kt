package org.dbs.store.consts

import org.dbs.consts.RestHttpConsts.URI_API
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.RouteVersion.URI_V1
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.RouteVersion.URI_V2


class SmartSaveSchoolMediaConsts {

    object Main {
        const val PROFILE = "config.restful.profile.name"
    }

    object RouteVersion {
        const val URI_V1 = "/v1"
        const val URI_V2 = "/v2"
    }

    object RouteTags {
        const val ROUTE_TAG_MEDIA_FILE = "Media file actions"
    }

    object Routes {
        const val URI_MEDIA_FILE = "/mediaFile"
        const val URI_UPLOAD = "/upload"
        const val URI_DOWNLOAD = "/download"

        const val ROUTE_UPLOAD_MEDIA_FILE = URI_API + URI_MEDIA_FILE + URI_V1 + URI_UPLOAD
        const val ROUTE_UPLOAD_MEDIA_FILE_V2 = URI_API + URI_MEDIA_FILE + URI_V2 + URI_UPLOAD // white point
        const val ROUTE_DOWNLOAD_MEDIA_FILE = URI_API + URI_MEDIA_FILE + URI_V1 + URI_DOWNLOAD
    }
}
