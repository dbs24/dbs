package org.dbs.rest.api.consts

import org.dbs.application.core.service.funcs.Patterns.V6_EXT
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.service.value.AbstractHttpRequestProcessor
import org.dbs.spring.core.api.EntityInfo

typealias BaseResp = HttpResponseBody<*>
typealias RequestId = String
typealias H1_PROCESSOR<R> = AbstractHttpRequestProcessor<R>

object RestApiConst {
    val NULL_ENTITY_INFO: EntityInfo? = null

    object Headers {
        const val X_REAL_IP = "X-Real-IP"
        const val X_FORWARDED_FOR = "X-Forwarded-For"
        val commonIpRegex by lazy { Regex("[^a-f0-9\\.\\:]") }
        val allowedIpV4Regex by lazy { Regex("[^0-9\\.]") }
        val allowedIpV6Regex by lazy { Regex("[$V6_EXT]") }
    }
}
