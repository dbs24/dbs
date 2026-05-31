package org.dbs.rest.api.consts

import org.dbs.application.core.service.funcs.Patterns.V6_EXT

object RestApiConst {

    object Headers {
        const val X_REAL_IP = "X-Real-IP"
        const val X_FORWARDED_FOR = "X-Forwarded-For"
        val commonIpRegex by lazy { Regex("[^a-f0-9\\.\\:]") }
        val allowedIpV4Regex by lazy { Regex("[^0-9\\.]") }
        val allowedIpV6Regex by lazy { Regex("[$V6_EXT]") }
    }
}
