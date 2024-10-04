package org.dbs.mgmt

import org.dbs.consts.GrpcConsts.GRPC_SET
import org.dbs.consts.SecurityConsts.Cors.CORS_CONFIG_SET
import org.dbs.consts.SpringCoreConst.GOOGLE_CLIENT_SET
import org.dbs.consts.SpringCoreConst.KAFKA_SET
import org.dbs.consts.SpringCoreConst.R2DBC_SET
import org.dbs.consts.SpringCoreConst.REDIS_SET
import org.dbs.consts.SpringCoreConst.WEB_SET
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.sandbox.consts.SandBoxGrpcConsts.GRPC_CC_SANDBOX_CLIENT_SET
import org.dbs.spring.boot.api.AbstractSpringBootApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = [ALL_PACKAGES])
class MgmtApplication : AbstractSpringBootApplication() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runSpringBootApplication(args, MgmtApplication::class)
        { WEB_SET + GOOGLE_CLIENT_SET + R2DBC_SET + KAFKA_SET + REDIS_SET + CORS_CONFIG_SET + GRPC_SET + GRPC_CC_SANDBOX_CLIENT_SET }
    }
}
