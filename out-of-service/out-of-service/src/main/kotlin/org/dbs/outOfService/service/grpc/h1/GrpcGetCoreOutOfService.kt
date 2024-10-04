package org.dbs.outOfService.service.grpc.h1

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.enums.I18NEnum.NOT_FOUND_ENTITY
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.outOfService.model.CoreOutOfService
import org.dbs.outOfService.service.grpc.OutOfServiceGrpcService
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.S3_CORE_OUT_OF_SERVICE
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.core.MainResponse as RESP
import org.dbs.protobuf.outOfService.GetCoreOutOfServiceDto as ENT
import org.dbs.protobuf.outOfService.GetCoreOutOfServiceRequest as REQ

object GrpcGetCoreOutOfService {
    suspend fun OutOfServiceGrpcService.getCoreOutOfServiceInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run {

        val entityBuilder by lazy { ENT.newBuilder() }
        validateRemoteAddress(remoteAddress)
        val coreOutOfService by lazy { LateInitVal<CoreOutOfService>() }

        buildGrpcResponseOld {
            it.run {
                //======================================================================================================
                fun validateRequestData(): Boolean = run {
                    noErrors()
                }

                //======================================================================================================
                fun findCoreOutOfService(): MonoRAB = fmStart {
                    coreOutOfServiceService.findCoreOutOfService()
                        .collectList()
                        .flatMap { coreOutOfServices ->

                            if (coreOutOfServices.isEmpty()) {
                                addErrorInfo(
                                    RC_INVALID_RESPONSE_DATA,
                                    INVALID_ENTITY_ATTR,
                                    S3_CORE_OUT_OF_SERVICE,
                                    findI18nMessage(NOT_FOUND_ENTITY)
                                )
                            }

                            coreOutOfService.init(coreOutOfServices.last())
                            this.toMono()
                        }
                }

                //======================================================================================================
                fun Mono<RAB>.finishResponseEntity() = map {
                    it.also {
                        coreOutOfService.value.apply {
                            entityBuilder.setUpdateDate(actualDate?.toLong() ?: 0L)
                                .setStartDate(serviceDateStart?.toLong() ?: 0L)
                                .setFinishDate(serviceDateFinish?.toLong() ?: 0L)
                                .setNote(note ?: EMPTY_STRING)
                        }
                    }
                }
                //======================================================================================================
                // validate requestData
                if (validateRequestData()) {
                    // process endpoint
                    processGrpcResponse {
                        findCoreOutOfService()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }
    }
}
