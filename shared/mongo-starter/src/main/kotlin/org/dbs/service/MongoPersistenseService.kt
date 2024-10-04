package org.dbs.service

import org.dbs.consts.RestHttpConsts.URI_HTTPS
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.service.consts.MongoConsts.MONGO_URI
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Lazy(false)
@Service
class MongoPersistenseService : AbstractApplicationService() {

    @Value("\${spring.data.mongodb.uri}")
    private val uri = EMPTY_STRING
    override fun initialize() = super.initialize().also {
        addUrl4LivenessTracking(uri.replace(MONGO_URI, URI_HTTPS), "MongoDb host")
    }
}
