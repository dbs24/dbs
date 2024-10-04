package org.dbs.tasker

import org.dbs.consts.SpringCoreConst.KAFKA_SET
import org.dbs.consts.SecurityConsts.Cors.CORS_CONFIG_SET
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.spring.boot.api.AbstractSpringBootApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TaskerApplication : AbstractSpringBootApplication() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runSpringBootApplication(args, TaskerApplication::class.java)
        { KAFKA_SET }
    }
}
