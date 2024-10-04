package org.dbs.spring.boot.banner

import org.dbs.consts.StringMap


fun interface BannerInititializer {
    fun initialize(): StringMap
}
