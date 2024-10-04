package org.dbs.sandbox.consts

import org.dbs.consts.SpringCoreConst.DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.SANDBOX_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SANDBOX_SERVICE_PORT
import org.dbs.consts.SysConst.EMPTY_STRING

object SandBoxGrpcConsts {

    val GRPC_CC_SANDBOX_CLIENT_SET =
        mapOf(
            "GRPC Sandbox client host" to SANDBOX_SERVICE_HOST,
            "GRPC Sandbox client port" to SANDBOX_SERVICE_PORT,
            DELIMITER to EMPTY_STRING
        )
}
