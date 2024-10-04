package org.dbs.auth.server.config

import org.dbs.auth.server.convert.ApplicationConverter
import org.dbs.auth.server.convert.ApplicationEnumConverter
import org.dbs.service.GenericEntityR2dbcConfiguration
import org.springframework.stereotype.Service

@Service
class PgR2dbcConfiguration : GenericEntityR2dbcConfiguration() {
    override fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        with(converters) {
            super.addExtraCustomConverters(this)
            add(ApplicationEnumConverter())
            add(ApplicationConverter())
        }
    }
}
