package org.dbs.goods.consts

import org.dbs.consts.SpringCoreConst.PropertiesNames.USER_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.USER_SERVICE_PORT

object MgmtGrpcConsts {

    val GRPC_CC_CLIENT_SET =
        mapOf(
            "GRPC user client host" to USER_SERVICE_HOST,
            "GRPC user client port" to USER_SERVICE_PORT
        )
}
