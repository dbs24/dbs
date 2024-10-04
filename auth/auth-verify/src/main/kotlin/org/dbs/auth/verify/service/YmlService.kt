package org.dbs.auth.verify.service

import org.dbs.auth.verify.consts.AuthVerifyConsts.YML_AUTH_A1_CACHE
import org.dbs.consts.SysConst.STRING_FALSE
import org.dbs.spring.core.api.ApplicationYmlService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class YmlService : ApplicationYmlService() {

    @Value("\${$YML_AUTH_A1_CACHE:$STRING_FALSE}")
    val cacheA1 = false

}