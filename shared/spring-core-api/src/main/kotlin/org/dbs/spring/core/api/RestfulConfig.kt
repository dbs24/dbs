package org.dbs.spring.core.api

import org.dbs.consts.SpringCoreConst.PropertiesNames.QUERY_MAX_EXEC_TIME_VALUE
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties("config.restful")
class RestfulConfig {
    var query: Query = Query()
    var message: Message = Message()

    class Query {
        var maxTimeExec: Int = QUERY_MAX_EXEC_TIME_VALUE
    }

    class Message {
        var printEntityId: Boolean = false
    }
}
