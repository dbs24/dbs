package org.dbs.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.BROWSER_HEADERS_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_BREAK
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_YML
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS_BREAK
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HOSTS_YML
import org.dbs.consts.SysConst.MASK_ALL
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class HeaderService : AbstractApplicationService() {
    @Value("\${$SECURITY_WHITE_HEADERS_YML:$SECURITY_WHITE_HEADERS_LIST}")
    val whiteHeaders = SECURITY_WHITE_HEADERS_LIST

    @Value("\${$SECURITY_WHITE_HOSTS_YML:$MASK_ALL}")
    val whiteHosts = MASK_ALL

    @Value("\${$SECURITY_WHITE_HEADERS_BREAK:$STRING_FALSE}")
    val breakIllegalHeader = false

    @Value("\${$SECURITY_WHITE_HOSTS_BREAK:$STRING_FALSE}")
    val breakIllegalHost = false

    val whiteHeadersLists by lazy {
        whiteHeaders.trim().lowercase()
            .plus(BROWSER_HEADERS_LIST.lowercase())
            .replace(" ", "")
            .split(",")
    }

    val whiteHostsLists by lazy {
        whiteHosts.trim().lowercase()
            .replace(" ", "")
            .split(",")
    }

}
