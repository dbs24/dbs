package org.dbs.mail

import org.dbs.consts.SpringCoreConst.KAFKA_SET
import org.dbs.consts.SpringCoreConst.MAIL_SET
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.spring.boot.api.AbstractSpringBootApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ComponentScan(basePackages = [ALL_PACKAGES])
@EnableScheduling
class MailServerApplication : AbstractSpringBootApplication() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runSpringBootApplication(args, MailServerApplication::class) {
            MAIL_SET + KAFKA_SET
        }
    }
}
