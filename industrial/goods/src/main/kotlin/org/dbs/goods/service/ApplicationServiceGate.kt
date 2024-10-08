package org.dbs.goods.service

import org.dbs.goods.service.grpc.GoodsGrpcService
import org.dbs.rest.service.RestFulService
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.ServiceLocator.findService

interface ApplicationServiceGate {
    companion object ServicesList {
        val restFulService by lazy { findService(RestFulService::class) }
        val userService by lazy { findService(UserService::class) }
        val userGrpcService by lazy { findService(GoodsGrpcService::class) }
        val r2dbcPersistenceService by lazy { findService(R2dbcPersistenceService::class) }
    }
}
