package org.dbs.mgmt.service.grpc.h1.convert

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.lobby.dto.CreatedLobbyStatusDto as H1E
import org.dbs.lobby.dto.UpdateLobbyStatusDto as H1IN
import org.dbs.lobby.dto.UpdateLobbyStatusResponse as H1OUT
import org.dbs.mgmt.client.CreatedLobbyStatus as H2E
import org.dbs.mgmt.client.UpdateLobbyStatusRequest as H2IN
import org.dbs.protobuf.core.MainResponse as H2OUT

@JvmInline
value class UpdateLobbyStatusConverter(override val entClass: KClass<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E>, Logging {
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .setModifiedCode(lobbyCode)
            .setStatus(newStatus)
            .build()
    }
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(modifiedCode, newStatus) }
    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
