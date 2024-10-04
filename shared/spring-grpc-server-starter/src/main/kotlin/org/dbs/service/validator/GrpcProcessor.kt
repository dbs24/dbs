package org.dbs.service.validator

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.NoArg2Mono
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_INTERNAL_ERROR
import org.dbs.reactor.MonoSyncSubscriber
import org.dbs.service.MonoRAB
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.validator.Error.GRPC_INTERNAL_ERROR
import org.dbs.validator.Field.UNKNOWN_FIELD
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.toMono

object GrpcProcessor : Logging {
    fun RAB.processGrpcResponse(func: NoArg2Mono<RAB>): Unit = if (noErrors()) {
        MonoSyncSubscriber(func()
            .doOnError { logger.error { it }
                addErrorInfo(
                    RC_INTERNAL_ERROR,
                    GRPC_INTERNAL_ERROR,
                    UNKNOWN_FIELD,
                    it.toString()
                )
            }
            .onErrorResume {
                logger.error { it }
                addErrorInfo(
                    RC_INTERNAL_ERROR,
                    GRPC_INTERNAL_ERROR,
                    UNKNOWN_FIELD,
                    it.toString()
                )
                empty()
            }
        ).doSubscribe()
    } else Unit

    fun <T> Int.noEmpty(rab: RAB, func: NoArg2Mono<T>): MonoRAB =
        if (this > 0) func().map { rab } else rab.toMono()

}
