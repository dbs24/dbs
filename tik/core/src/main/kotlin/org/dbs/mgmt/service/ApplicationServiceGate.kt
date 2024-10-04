package org.dbs.mgmt.service

import org.dbs.mgmt.service.grpc.MgmtGrpcService
import org.dbs.mgmt.service.user.UserService
import org.dbs.rest.service.RestFulService
import org.dbs.service.v2.R2dbcPersistenceService
import org.dbs.spring.core.api.ServiceLocator.findService

interface ApplicationServiceGate {
    companion object ServicesList {
        val userService by lazy { findService(UserService::class) }
        val restFulService by lazy { findService(RestFulService::class) }
        val mgmtGrpcService by lazy { findService(MgmtGrpcService::class) }
        val r2dbcPersistenceService by lazy { findService(R2dbcPersistenceService::class) }
    }
}
