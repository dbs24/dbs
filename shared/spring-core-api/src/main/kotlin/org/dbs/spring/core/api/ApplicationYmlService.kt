package org.dbs.spring.core.api

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_MESSAGE_PRINT_ENTITY_ID
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SERVER_PORT
import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_SERVER_PORT_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.DEV_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.JUNIT_MODE
import org.dbs.consts.SpringCoreConst.PropertiesNames.QUERY_MAX_EXEC_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.QUERY_MAX_EXEC_TIME_VALUE
import org.dbs.consts.SpringCoreConst.PropertiesNames.REFERENCES_AUTO_SYNCHRONIZE
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_ABUSED_QP_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_BREAK
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_YML
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS_BREAK
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS_YML
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_MESSAGE_PRINT_ENTITY_ID
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_EVICT_IN_BACKGROUND
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_EVICT_IN_BACKGROUND_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_CONNECTIONS
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_CONNECTIONS_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_IDLE_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_IDLE_TIME_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_LIFE_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_MAX_LIFE_TIME_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT_DEF
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_TEST_CONNECTION
import org.dbs.consts.SpringCoreConst.PropertiesNames.WEB_CLIENT_TEST_CONNECTION_DEF
import org.dbs.consts.SysConst.ABUSED_QP_DEF
import org.dbs.consts.SysConst.MASK_ALL
import org.dbs.consts.SysConst.STRING_FALSE
import org.springframework.beans.factory.annotation.Value

abstract class ApplicationYmlService : AbstractApplicationBean() {

    // devMode
    @Value("\${${DEV_MODE}:$STRING_FALSE}")
    val devMode = false

    @Value("\${$JUNIT_MODE:$STRING_FALSE}")
    val junitMode: Boolean = false

    // generic
    @Value("\${${CONFIG_SERVER_PORT}:$CONFIG_SERVER_PORT_DEF")
    val serverPort = CONFIG_SERVER_PORT_DEF

    @Value("\${$CONFIG_RESTFUL_MESSAGE_PRINT_ENTITY_ID:$VALUE_RESTFUL_MESSAGE_PRINT_ENTITY_ID}")
    val printEntityId = false

    @Value("\${$REFERENCES_AUTO_SYNCHRONIZE:true}")
    val autoSynchronize = true

    @Value("\${$QUERY_MAX_EXEC_TIME:$QUERY_MAX_EXEC_TIME_VALUE}")
    val queryMaxTimeExec = QUERY_MAX_EXEC_TIME_VALUE

    @Value("\${$WEB_CLIENT_MAX_CONNECTIONS:$WEB_CLIENT_MAX_CONNECTIONS_DEF}")
    val maxConnections = WEB_CLIENT_MAX_CONNECTIONS_DEF

    @Value("\${$WEB_CLIENT_TEST_CONNECTION:$WEB_CLIENT_TEST_CONNECTION_DEF}")
    val testConnection = WEB_CLIENT_TEST_CONNECTION_DEF

    @Value("\${$WEB_CLIENT_MAX_IDLE_TIME:$WEB_CLIENT_MAX_IDLE_TIME_DEF}")
    val maxIdleTime = WEB_CLIENT_MAX_IDLE_TIME_DEF

    @Value("\${$WEB_CLIENT_MAX_LIFE_TIME:$WEB_CLIENT_MAX_LIFE_TIME_DEF}")
    val maxLifeTime = WEB_CLIENT_MAX_LIFE_TIME_DEF

    @Value("\${$WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT:$WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT_DEF}")
    val pendingAcquireTimeout = WEB_CLIENT_PENDING_ACQUIRE_TIMEOUT_DEF

    @Value("\${$WEB_CLIENT_EVICT_IN_BACKGROUND:$WEB_CLIENT_EVICT_IN_BACKGROUND_DEF}")
    val evictInBackground = WEB_CLIENT_EVICT_IN_BACKGROUND_DEF
    // headers
    @Value("\${$SECURITY_WHITE_HEADERS_YML:$SECURITY_WHITE_HEADERS_LIST}")
    val whiteHeaders = SECURITY_WHITE_HEADERS_LIST

    @Value("\${$SECURITY_WHITE_HOSTS_YML:$MASK_ALL}")
    val whiteHosts = MASK_ALL

    @Value("\${$SECURITY_WHITE_HEADERS_BREAK:$STRING_FALSE}")
    val breakIllegalHeader = false

    @Value("\${$SECURITY_WHITE_HOSTS_BREAK:$STRING_FALSE}")
    val breakIllegalHost = false

    @Value("\${$SECURITY_ABUSED_QP_LIST:$ABUSED_QP_DEF}")
    val abusedQueryParamsValues: Collection<String> = createCollection()

}
