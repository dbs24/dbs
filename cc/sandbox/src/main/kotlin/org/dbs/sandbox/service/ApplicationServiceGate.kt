package org.dbs.sandbox.service

import org.dbs.rest.service.RestFulService
import org.dbs.sandbox.service.grpc.SandBoxGrpcService
import org.dbs.spring.core.api.ServiceLocator.findService

interface ApplicationServiceGate {

    companion object ServicesList {
        val restFulService by lazy { findService(RestFulService::class) }
        val inviteService by lazy { findService(InviteService::class) }
        val sandBoxGrpcService by lazy { findService(SandBoxGrpcService::class) }
    }
}
