package org.dbs.sandbox.service.grpc.h1.convert

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.invite.dto.invite.CreatedInviteStatusDto as H1E
import org.dbs.invite.dto.invite.UpdateInviteStatusDto as H1IN
import org.dbs.invite.dto.invite.UpdateInviteStatusResponse as H1OUT
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.sandbox.invite.client.CreatedInviteStatus as H2E
import org.dbs.sandbox.invite.client.UpdateInviteStatusRequest as H2IN

@JvmInline
value class UpdateInviteStatusConverter(override val entClass: KClass<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E>, Logging {
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .setInviteCode(inviteCode)
            .setStatus(newStatus)
            .build()
    }
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(inviteCode, newStatus) }
    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
