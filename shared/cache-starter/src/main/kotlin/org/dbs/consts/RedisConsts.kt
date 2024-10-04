package org.dbs.consts

import org.dbs.consts.SpringCoreConst.DELIMITER
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_REDIS_PORT
import org.dbs.consts.SysConst.EMPTY_STRING

object RedisConsts {
    val REDIS_DB_SET = mapOf(
        "Redis host " to SPRING_REDIS_HOST,
        "Redis port " to SPRING_REDIS_PORT,
        DELIMITER to EMPTY_STRING
        )
}
