package org.dbs.service

import org.dbs.service.convert.PrivilegeGroupConverter
import org.dbs.service.convert.PrivilegeGroupEnumConverter

abstract class GenericEntityR2dbcConfiguration : PostgresR2dbcConfiguration() {
    override fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        with(converters) {
            //-----------------------------------------------
            //add(CurrencyEnumConverter())
            //add(CurrencyConverter())
            //-----------------------------------------------
            add(PrivilegeGroupEnumConverter())
            add(PrivilegeGroupConverter())
        }
    }
}
