package org.dbs.analyst.service.grpc.h1.convert

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import org.dbs.cm.client.CreatePlayerResponse as H2OUT
import org.dbs.cm.client.CreateOrUpdatePlayerRequest as H2IN
import org.dbs.cm.client.CreatedPlayerDto as H2E
import org.dbs.player.dto.player.CreatePlayerResponse as H1OUT
import org.dbs.player.dto.player.CreateOrUpdatePlayerDto as H1IN
import org.dbs.player.dto.player.CreatedPlayerDto as H1E

@JvmInline
value class CreateOrUpdatePlayerConverter(override val entClass: Class<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E>, Logging {
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .also { oldLogin?.apply { it.setOldLogin(this) } }
            .also { oldEmail?.apply { it.setOldEmail(this) } }
            .also { phone?.apply { it.setPhone(this) } }
            .setLogin(login)
            .setEmail(email)
            .also { password?.apply { it.setPassword(this) } }
            .setFirstName(firstName)
            .setLastName(lastName)
            .build()
    }
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(playerLogin, email) }
    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
