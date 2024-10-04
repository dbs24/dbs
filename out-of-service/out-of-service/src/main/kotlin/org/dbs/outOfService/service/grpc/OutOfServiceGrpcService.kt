package org.dbs.outOfService.service.grpc

import kotlinx.coroutines.coroutineScope
import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.outOfService.service.CoreOutOfServiceService
import org.dbs.outOfService.service.grpc.h1.GrpcCreateOrUpdateCoreOutOfService.createOrUpdateCoreOutOfServiceInternal
import org.dbs.protobuf.core.MainResponse
import org.dbs.protobuf.outOfService.CreateOrUpdateCoreOutOfServiceRequest
import org.dbs.protobuf.outOfService.OutOfServiceClientServiceGrpcKt
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.stereotype.Service

@Service
class OutOfServiceGrpcService(
    val coreOutOfServiceService: CoreOutOfServiceService,
) : AbstractGrpcServerService(), PublicApplicationBean {

    @GrpcService
    inner class OutOfServiceService : OutOfServiceClientServiceGrpcKt.OutOfServiceClientServiceCoroutineImplBase(), PublicApplicationBean {

        override suspend fun createOrUpdateCoreOutOfService(request: CreateOrUpdateCoreOutOfServiceRequest) =
            createOrUpdateCoreOutOfServiceInternal(request)

    }
}
