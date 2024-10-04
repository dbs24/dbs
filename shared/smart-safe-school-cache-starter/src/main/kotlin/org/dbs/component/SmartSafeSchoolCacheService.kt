package org.dbs.component

import org.dbs.consts.SpringCoreConst.PropertiesNames.CONFIG_RESTFUL_SECURITY_SSS_SERVER
import org.dbs.consts.SpringCoreConst.PropertiesNames.VALUE_RESTFUL_SECURITY_SSS_SERVER
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.service.RedisUtil
import org.dbs.spring.core.api.AbstractWebClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
@Suppress(UNCHECKED_CAST)
class SmartSafeSchoolCacheService internal constructor(redisTemplate: RedisTemplate<String, Any>) :
    AbstractWebClientService() /*, ServiceCache*/ {
    @Suppress("UNCHECKED_CAST")
    private val redisUtil: RedisUtil<String, Collection<String>> =
        RedisUtil(redisTemplate as RedisTemplate<String, Collection<String>>)

    @Value("\${$CONFIG_RESTFUL_SECURITY_SSS_SERVER:$VALUE_RESTFUL_SECURITY_SSS_SERVER}")
    private val mainServer = EMPTY_STRING

    var casheUserManagementJwt: String = EMPTY_STRING

    @Override
    override fun initialize() = super.initialize().also { prepareDefaultWebClient(mainServer) }

    //==================================================================================================================
    // User 2 Role

}
