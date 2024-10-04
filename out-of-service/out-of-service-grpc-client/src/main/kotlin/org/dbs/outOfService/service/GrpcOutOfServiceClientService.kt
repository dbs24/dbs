package org.dbs.outOfService.service

import org.dbs.consts.OperDateDtoNull
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.consts.SpringCoreConst.PropertiesNames.OUT_OF_SERVICE_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.OUT_OF_SERVICE_SERVICE_PORT
import org.dbs.consts.StringNoteNull
import org.dbs.protobuf.outOfService.CreateOrUpdateCoreOutOfServiceRequest
import org.dbs.protobuf.outOfService.GetCoreOutOfServiceRequest
import org.dbs.protobuf.outOfService.OutOfServiceClientServiceGrpcKt
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcOutOfServiceClientService(
    @Value("\${$OUT_OF_SERVICE_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$OUT_OF_SERVICE_SERVICE_PORT:6453}")
    private val grpcPort: Int,
) : AbstractGrpcClientService<OutOfServiceClientServiceGrpcKt.OutOfServiceClientServiceCoroutineStub>(
    grpcUrl,
    grpcPort
) {

    suspend fun createOrUpdateCoreOutOfService(
        serviceDateStart: OperDateDtoNull,
        serviceDateFinish: OperDateDtoNull,
        note: StringNoteNull
    ) = grpcCall {
        it.createOrUpdateCoreOutOfService(
            CreateOrUpdateCoreOutOfServiceRequest.newBuilder()
                .also { serviceDateStart?.apply { it.setServiceDateStart(serviceDateStart) } }
                .also { serviceDateFinish?.apply { it.setServiceDateFinish(serviceDateFinish) } }
                .setNote(note)
                .build()
        ).also { logger.debug { "receive core out of service: ${it.responseAnswer}" } }
    }

    suspend fun getCoreOutOfService() = grpcCall {
        it.getCoreOutOfService(
            GetCoreOutOfServiceRequest.newBuilder()
                .build()
        ).also { logger.debug { "receive core out of service: ${it.responseAnswer}" } }
    }
}
