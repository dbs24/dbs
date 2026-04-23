package org.dbs.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.BROWSER_HEADERS_LIST
import org.dbs.consts.SpringCoreConst.PropertiesNames.SECURITY_WHITE_HEADERS_LIST
import org.dbs.consts.SysConst.MASK_ALL
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service

@Service
@ConfigurationProperties("config.security.h1")
class HeaderService : AbstractApplicationService() {

    var whiteHeaders: String = SECURITY_WHITE_HEADERS_LIST
    var whiteHosts: String = MASK_ALL
    var breakIllegalHeader: Boolean = false
    var breakIllegalHost: Boolean = false
    var abused: Abused = Abused()

    class Abused {
        var queryParamsValues: Collection<String> = emptyList()
    }

    val abusedQueryParamsValues: Collection<String> get() = abused.queryParamsValues

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
