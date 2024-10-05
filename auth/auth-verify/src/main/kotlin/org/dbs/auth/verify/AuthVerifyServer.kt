package org.dbs.auth.verify

import org.dbs.auth.consts.AuthServerGrpcConsts.GRPC_AUTH_SERVER_CLIENT_SET
import org.dbs.consts.GrpcConsts.GRPC_SET
import org.dbs.consts.SecurityConsts.Cors.CORS_CONFIG_SET
import org.dbs.consts.SpringCoreConst.JWT_SET
import org.dbs.consts.SpringCoreConst.KAFKA_SET
import org.dbs.consts.SpringCoreConst.WEB_SET
import org.dbs.spring.boot.api.AbstractSpringBootApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class AuthVerifyServer : AbstractSpringBootApplication() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runSpringBootApplication(args, AuthVerifyServer::class)
        { WEB_SET + KAFKA_SET + JWT_SET + GRPC_AUTH_SERVER_CLIENT_SET + CORS_CONFIG_SET + GRPC_SET}
    }
}
