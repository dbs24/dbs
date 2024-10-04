package org.dbs.actor.consts

import org.dbs.consts.SpringCoreConst.PropertiesNames.CM_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.CM_SERVICE_PORT

object CmGrpcConsts {

    val GRPC_CM_CLIENT_SET =
        mapOf(
            "GRPC CM client host" to CM_SERVICE_HOST,
            "GRPC CM client port" to CM_SERVICE_PORT
        )
}
