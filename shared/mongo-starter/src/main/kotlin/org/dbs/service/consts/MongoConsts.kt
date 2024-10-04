package org.dbs.service.consts

import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_DATA_MONGODB_URI


typealias NoSqlEntityId = String

object MongoConsts {
    const val MONGO_URI = "mongodb://"

    val MONGO_DB_SET = mapOf("database" to SPRING_DATA_MONGODB_URI)

}
