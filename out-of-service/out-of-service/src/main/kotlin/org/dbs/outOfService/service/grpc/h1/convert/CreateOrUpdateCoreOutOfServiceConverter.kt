package org.dbs.outOfService.service.grpc.h1.convert

import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceDto as H1IN
import org.dbs.outOfService.dto.CreateOrUpdateCoreOutOfServiceResponse as H1OUT
import org.dbs.outOfService.dto.CreatedCoreOutOfServiceRespDto as H1E
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.protobuf.outOfService.CreateOrUpdateCoreOutOfServiceRequest as H2IN
import org.dbs.protobuf.outOfService.CreatedCoreOutOfServiceRespDto as H2E

@JvmInline
value class CreateOrUpdateCoreOutOfServiceConverter(override val entClass: KClass<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E> {
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN
            .newBuilder()
            .also { serviceDateStart?.apply { it.setServiceDateStart(this) } }
            .also { serviceDateFinish?.apply { it.setServiceDateFinish(this) } }
            .also { note?.apply { it.setNote(this) } }
            .build()
    }

    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(serviceDateStart.takeIf { it != 0L }, serviceDateFinish.takeIf { it != 0L }) }

    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
