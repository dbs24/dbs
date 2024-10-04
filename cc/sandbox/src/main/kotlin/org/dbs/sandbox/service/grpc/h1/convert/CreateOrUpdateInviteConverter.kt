package org.dbs.sandbox.service.grpc.h1.convert

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.invite.dto.invite.CreateInviteResponse as H1OUT
import org.dbs.invite.dto.invite.CreateOrUpdateInviteDto as H1IN
import org.dbs.invite.dto.invite.CreatedInviteDto as H1E
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.sandbox.invite.client.CreateOrUpdateInviteRequest as H2IN
import org.dbs.sandbox.invite.client.CreatedInviteDto as H2E

@JvmInline
value class CreateOrUpdateInviteConverter(override val entClass: KClass<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E>, Logging {
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .also { inviteCode?.apply { it.setInviteCode(this) } }
            .also { playerLogin.apply { it.setPlayerLogin(this) } }
            .also { gameType.apply { it.setGameType(this) } }
            .also { validDate.apply { it.setValidDate(this) } }
            .also { requiredRating?.apply { it.setRequiredRating(this) } }
            .also { whiteSide?.apply { it.setWhiteSide(this) } }
            .build()
    }
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(inviteCode, playerLogin, status) }
    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
