package org.dbs.mgmt.consts

import org.dbs.consts.SpringCoreConst.PropertiesNames.MGMT_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.MGMT_SERVICE_PORT

object MgmtGrpcConsts {

    val GRPC_CC_CLIENT_SET =
        mapOf(
            "GRPC CC client host" to MGMT_SERVICE_HOST,
            "GRPC CC client port" to MGMT_SERVICE_PORT
        )
}
