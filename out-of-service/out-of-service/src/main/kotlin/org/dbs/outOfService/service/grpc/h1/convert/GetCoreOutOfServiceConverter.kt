package org.dbs.outOfService.service.grpc.h1.convert

import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.outOfService.dto.GetCoreOutOfServiceDto as H1E
import org.dbs.outOfService.dto.GetCoreOutOfServiceRequest as H1IN
import org.dbs.outOfService.dto.GetCoreOutOfServiceResponse as H1OUT
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.protobuf.outOfService.GetCoreOutOfServiceDto as H2E
import org.dbs.protobuf.outOfService.GetCoreOutOfServiceRequest as H2IN

@JvmInline
value class GetCoreOutOfServiceConverter(override val entClass: KClass<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E> {
    override fun buildEntityH1(h2: H2E): H1E = h2.run {
        H1E(updateDate.takeIf { it != 0L }, startDate.takeIf { it != 0L }, finishDate.takeIf { it != 0L }, note)
    }

    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN
            .newBuilder()
            .build()
    }

    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
