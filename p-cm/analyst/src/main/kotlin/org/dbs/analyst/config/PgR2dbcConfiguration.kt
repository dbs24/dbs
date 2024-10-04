package org.dbs.analyst.config

import org.dbs.analyst.dao.convert.PlayerStatusConverter
import org.dbs.analyst.dao.convert.PlayerStatusEnumConverter
import org.dbs.analyst.dao.convert.SolutionStatusConverter
import org.dbs.analyst.dao.convert.SolutionStatusEnumConverter
import org.dbs.service.PostgresR2dbcConfiguration
import org.springframework.stereotype.Service

@Service
class PgR2dbcConfiguration : PostgresR2dbcConfiguration() {
    override fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        with(converters) {
            logger.info("addExtraCustomConverters")
            add(PlayerStatusConverter())
            add(PlayerStatusEnumConverter())
            add(SolutionStatusConverter())
            add(SolutionStatusEnumConverter())
        }
    }
}
