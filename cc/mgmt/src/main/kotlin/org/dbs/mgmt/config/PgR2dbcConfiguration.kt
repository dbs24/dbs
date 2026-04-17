package org.dbs.mgmt.config

import org.dbs.service.PostgresR2dbcConfiguration
import org.dbs.service.convert.EntityStatusReadConverter
import org.dbs.service.convert.EntityStatusWriteConverter
import org.springframework.stereotype.Service

@Service
class PgR2dbcConfiguration : PostgresR2dbcConfiguration() {
    override fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        with(converters) {
            add(EntityStatusReadConverter())
            add(EntityStatusWriteConverter())
        }
    }
}
