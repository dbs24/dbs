package org.dbs.spring.core.api

import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_EVICT_IN_BACKGROUND_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_CONNECTIONS_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_IDLE_TIME_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_LIFE_TIME_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_TEST_CONNECTION_DEF
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties("config.webclient")
class WebClientConfig {
    var maxConnections: Int = WEB_CLIENT_MAX_CONNECTIONS_DEF
    var testConnection: Boolean = WEB_CLIENT_TEST_CONNECTION_DEF
    var maxIdleTime: Int = WEB_CLIENT_MAX_IDLE_TIME_DEF
    var maxLifeTime: Int = WEB_CLIENT_MAX_LIFE_TIME_DEF
    var pendingAcquireTimeout: Int = WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT_DEF
    var evictInBackground: Int = WEB_CLIENT_EVICT_IN_BACKGROUND_DEF
}
