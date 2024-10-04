package org.dbs.outOfService.consts

import org.dbs.consts.SpringCoreConst.PropertiesNames.OUT_OF_SERVICE_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.OUT_OF_SERVICE_SERVICE_PORT

object OutOfServiceGrpcConsts {

    val GRPC_OUT_OF_SERVICE_CLIENT_SET =
        mapOf(
            "GRPC Out of service client host" to OUT_OF_SERVICE_SERVICE_HOST,
            "GRPC Out of service client port" to OUT_OF_SERVICE_SERVICE_PORT
        )
}
