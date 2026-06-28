package org.dbs.test.core

object SysTestConsts {

    const val TEST_REST_USER_AGENT = "Test Rest User Agent/1.0; x64"

    object Postgres {
        const val postresIsNotRunning = "PostgresContainer is not running"
        const val failedMsgTemplate = "Endpoint '%s' execution is failed"
        const val TEST_PG_R2DBC_IMAGE_TAG = "r2dbc:tc:postgresql:///cm_dev?TC_IMAGE_TAG=14"
    }

    object Grpc {
        const val GRPC_RANDOM_SERVER_PORT = "\${random.int[10000,32767]}"
    }

}
