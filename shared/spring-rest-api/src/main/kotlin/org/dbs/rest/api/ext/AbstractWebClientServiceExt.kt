package org.dbs.rest.api.ext

import org.dbs.application.core.service.funcs.Patterns.V4_PATTERN
import org.dbs.application.core.service.funcs.Patterns.V6_PATTERN
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_4_TEST
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV4Regex
import org.dbs.rest.api.consts.RestApiConst.Headers.allowedIpV6Regex
import org.dbs.rest.api.consts.RestApiConst.Headers.commonIpRegex

object AbstractWebClientServiceExt {

    fun String.isIpV4() = V4_PATTERN.matcher(this).matches()

    fun String.isIpV6() = V6_PATTERN.matcher(this).matches()

    fun String.isValidIp() = this.let { (it == URI_LOCALHOST_4_TEST) or it.isIpV4() or isIpV6() }

    fun String.ipAddress() =
        if (isValidIp()) this
        else replace("%0", EMPTY_STRING).lastIndexOf(":").let {
            val addr = (if (it > 0) {
                this.substring(0, it)
            } else
                this).replace(commonIpRegex, EMPTY_STRING)
            // v4
            if (V4_PATTERN.matcher(addr).matches()) {
                addr
            } else {
                // possible v6
                val updV6 = addr.replace(allowedIpV6Regex, EMPTY_STRING)
                if (V6_PATTERN.matcher(updV6).matches()) {
                    updV6
                } else {
                    addr
                }
            }
        }

    fun String.validIpV4() =
        (this.indexOf(":").let {
            if (it > 0) this.substring(0, it) else this
        }).replace(allowedIpV4Regex, EMPTY_STRING)

    fun String.subnet2IpV4() =
        (this.indexOf(".", this.indexOf(".") + 1).let {
            if (it > 0) this.substring(0, it) else this
        }).replace(allowedIpV4Regex, EMPTY_STRING)
}
