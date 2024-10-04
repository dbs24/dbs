package org.dbs.outOfService.service.grpc.h1

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.DATE_TIME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.NOTE_PATTERN
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.OperDateDto
import org.dbs.consts.StringNote
import org.dbs.consts.SysConst.LONG_ZERO
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmInTransaction
import org.dbs.ext.GrpcFuncs.fmRab
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.outOfService.mapper.CoreOutOfServiceMappers.createHist
import org.dbs.outOfService.mapper.CoreOutOfServiceMappers.updateCoreOutOfService
import org.dbs.outOfService.model.CoreOutOfService
import org.dbs.outOfService.service.grpc.OutOfServiceGrpcService
import org.dbs.service.MonoRAB
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.validateParamPatternIfPresent
import org.dbs.validator.Field.S3_CORE_OUT_OF_SERVICE_FINISH_DATE
import org.dbs.validator.Field.S3_CORE_OUT_OF_SERVICE_NOTE
import org.dbs.validator.Field.S3_CORE_OUT_OF_SERVICE_START_DATE
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.core.MainResponse as RESP
import org.dbs.protobuf.outOfService.CreateOrUpdateCoreOutOfServiceRequest as REQ
import org.dbs.protobuf.outOfService.CreatedCoreOutOfServiceRespDto as ENT

object GrpcCreateOrUpdateCoreOutOfService {
    suspend fun OutOfServiceGrpcService.createOrUpdateCoreOutOfServiceInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        val entityBuilder by lazy { ENT.newBuilder() }

        val coreOutOfService by lazy { LateInitVal<CoreOutOfService>() }
        val modifiedCoreOutOfService by lazy { LateInitVal<CoreOutOfService>() }
        val dateStart by lazy { LateInitVal<OperDateDto>() }
        val dateFinish by lazy { LateInitVal<OperDateDto>() }
        val noteDto by lazy { LateInitVal<StringNote>() }

        val justCreated by lazy { LateInitVal<Boolean>() }

        buildGrpcResponseOld {
            it.run {
                //======================================================================================================
                fun validateRequestData(): Boolean = run {

                    validateParamPatternIfPresent(
                        serviceDateStart.toString().grpcGetOrNull(),
                        DATE_TIME_PATTERN,
                        S3_CORE_OUT_OF_SERVICE_START_DATE
                    ) {
                        dateStart.init(it.toLong())
                    }

                    validateParamPatternIfPresent(
                        serviceDateFinish.toString().grpcGetOrNull(),
                        DATE_TIME_PATTERN,
                        S3_CORE_OUT_OF_SERVICE_FINISH_DATE
                    ) {
                        dateFinish.init(it.toLong())
                    }

                    validateParamPatternIfPresent(
                        note.grpcGetOrNull(),
                        NOTE_PATTERN,
                        S3_CORE_OUT_OF_SERVICE_NOTE
                    ) {
                        noteDto.init(it)
                    }
                    noErrors()
                }

                //======================================================================================================

                fun findOrCreateCoreOurOfService(): MonoRAB = fmStart {
                    coreOutOfServiceService.findCoreOutOfService()
                        .collectList()
                        .flatMap { coreOutOfServices ->
                            if (coreOutOfServices.isEmpty()) {
                                justCreated.init(true)
                                coreOutOfServiceService.createNewCoreOutOfService().toMono()
                            } else {
                                coreOutOfServices.last().toMono()
                            }
                        }
                        .flatMap { outOfService ->
                            coreOutOfService.init(outOfService); this.toMono()
                        }
                }

                //======================================================================================================

                fun MonoRAB.updateCoreOutOfService(): MonoRAB = fmRab {
                    modifiedCoreOutOfService.init(
                        coreOutOfServiceService.updateCoreOutOfService(
                            coreOutOfService.value,
                            dateStart.valueOrNull,
                            dateFinish.valueOrNull,
                            noteDto.valueOrNull
                        )
                    ).toMono()
                }

                //======================================================================================================

                fun MonoRAB.save() = fmInTransaction {
                    coreOutOfServiceService.executeActionHist(
                        coreOutOfServiceService.createHist(
                            coreOutOfService.value
                        ),
                        justCreated.valueOrDefault(false)
                    ).flatMap {
                        coreOutOfServiceService.executeAction(
                            modifiedCoreOutOfService.value,
                            justCreated.valueOrDefault(false)
                        )
                    }
                }

                //======================================================================================================

                fun Mono<RAB>.finishResponseEntity() = fmFinish {
                    it.also {
                        entityBuilder
                            .setServiceDateStart(dateStart.valueOrDefault(LONG_ZERO))
                            .setServiceDateFinish(dateFinish.valueOrDefault(LONG_ZERO))
                    }
                }
                //======================================================================================================
                // validate requestData
                if (validateRequestData()) {
                    // process endpoint
                    processGrpcResponse {
                        findOrCreateCoreOurOfService()
                            .updateCoreOutOfService()
                            .save()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }
    }
}
