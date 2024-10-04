package org.dbs.test.core

import org.dbs.rest.api.action.RestAction.MODIFY_ENTITY
import org.dbs.rest.api.action.SimpleActionInfo

object SysTestConsts {

    object Postgres {
        const val postresIsNotRunning = "PostgresContainer is not running"
        const val failedMsgTemplate = "Endpoint '%s' execution is failed"
        const val TEST_PG_R2DBC_IMAGE_TAG = "r2dbc:tc:postgresql:///cm_dev?TC_IMAGE_TAG=14"
    }

    object Grpc {
        const val GRPC_RANDOM_SERVER_PORT = "\${random.int[10000,32767]}"
    }
    val MODIFY_ACTION = SimpleActionInfo.createSimpleActionInfo(MODIFY_ENTITY)

}
