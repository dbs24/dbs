package org.dbs.grpc.api

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.grpc.consts.GM
import org.dbs.grpc.ext.ResponseAnswerObj.hasErrors
import org.dbs.grpc.ext.ResponseAnswerObj.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_OK
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_RESPONSE_DATA
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_OK
import org.dbs.validator.Error.GENERAL_ERROR
import org.dbs.validator.ErrorInfo
import org.dbs.validator.Field.UNKNOWN_FIELD
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.reflect.KClass
import kotlin.reflect.full.allSupertypes
import kotlin.reflect.jvm.jvmErasure
import org.dbs.protobuf.core.ResponseAnswer as RA
import org.dbs.rest.api.nio.HttpResponseBody as BODY
import org.dbs.rest.api.nio.ResponseDto as DTO

typealias H2H1<T, V> = (T, V) -> V
typealias GrpcAction<T, V> = suspend (V) -> T

interface H1h2<H1REQ, H2REQ : GM, H1RES : BODY<T>, H2RES : GM, T : DTO, V : GM> {

    val entClass: KClass<V>
    fun buildEntityH1(h2: V): T
    fun buildRequestH2(h1: H1REQ): H2REQ
    fun convertResponseH2H1(h2: H2RES, h1: H1RES): Mono<H1RES> = buildResponseH1()(h2, h1).toMono()
    fun convertErrors(h2: RA, h1: H1RES) = h1.apply {
        h2.run {
            error = errorMessage.takeIf { it.isNotEmpty() }
            message = responseCode.name
            this@apply.responseCode = if (responseCode == RC_OK) OC_OK else OC_INVALID_RESPONSE_DATA
            if (hasErrors()) {
                errors.also { errDts ->
                    errorMessagesList.forEach {
                        errDts.add(
                            ErrorInfo(
                                GENERAL_ERROR,
                                UNKNOWN_FIELD,
                                it.message
                            )
                        )
                    }
                }
            }
        }
    }

    fun buildResponseH1(): H2H1<H2RES, H1RES>
    fun h2h1(h2: RA, h1: H1RES) = h2.apply {
        if (noErrors()) {
            responseEntity.unpack(entClass.java).run {
                h1.responseEntity = buildEntityH1(this@run)
            }
        }
        convertErrors(h2, h1)
    }

    companion object: Logging {

        @Suppress(UNCHECKED_CAST)
        suspend fun <H1REQ, H2REQ : GM, H1RES : BODY<T>, H2RES : GM, T : DTO, V : GM,
                CNV : H1h2<H1REQ, H2REQ, H1RES, H2RES, T, V>> applyH1Converter(
            convClass: KClass<CNV>,
            requestDto: H1REQ,
            id: String,
            grpcAction: GrpcAction<H2RES, H2REQ>,
        ): Mono<H1RES> = run {
            val convKType = convClass.allSupertypes.first{ it.classifier == H1h2::class }
            val clazz: KClass<V> = convKType.arguments[5].type!!.jvmErasure as KClass<V>
            convClass.constructors.first().call(clazz).run {
                val respH1Class: KClass<H1RES> = convKType.arguments[2].type!!.jvmErasure as KClass<H1RES>
                logger.debug { requestDto }
                convertResponseH2H1(
                    grpcAction(buildRequestH2(requestDto)),
                    respH1Class.java.getDeclaredConstructor(String::class.java).newInstance(id)
                )
            }
        }
    }
}
