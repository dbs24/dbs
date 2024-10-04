package org.dbs.auth.consts

import org.dbs.consts.SpringCoreConst.PropertiesNames.AUTH_SERVER_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.AUTH_SERVER_SERVICE_PORT

object AuthServerGrpcConsts {

    val GRPC_AUTH_SERVER_CLIENT_SET =
        mapOf(
            "GRPC Auth-server client host" to AUTH_SERVER_SERVICE_HOST,
            "GRPC Auth-server port" to AUTH_SERVER_SERVICE_PORT
        )
}
