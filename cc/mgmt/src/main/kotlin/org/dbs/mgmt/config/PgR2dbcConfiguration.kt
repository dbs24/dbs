package org.dbs.mgmt.config

import org.dbs.service.PostgresR2dbcConfiguration
import org.springframework.stereotype.Service

@Service
class PgR2dbcConfiguration : PostgresR2dbcConfiguration() {
    override fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        with(converters) {
            logger.info("addExtraCustomConverters")
//            add(PlayerStatusConverter())
//            add(PlayerStatusEnumConverter())
//            add(SolutionStatusConverter())
//            add(SolutionStatusEnumConverter())
        }
    }
}
