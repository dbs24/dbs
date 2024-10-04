package org.dbs.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.BUCKET_4J_ENABLED
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_ENABLED_REFLECTION
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_MAX_EXEC_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_MAX_EXEC_TIME_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.GRPC_SERVER_SECURITY
import org.dbs.consts.SysConst.STRING_FALSE
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GrpcYmlConfig {
    @Value("\${$GRPC_MAX_EXEC_TIME:$GRPC_MAX_EXEC_TIME_VALUE}")
    val maxTimeExec = GRPC_MAX_EXEC_TIME_VALUE

    @Value("\${$GRPC_ENABLED_REFLECTION:$STRING_FALSE}")
    val enableGrpcReflection = false

    @Value("\${$BUCKET_4J_ENABLED:$STRING_FALSE}")
    val bucket4jEnabled = false

    @Value("\${$GRPC_SERVER_SECURITY:$STRING_FALSE}")
    val useSsl = false
}
