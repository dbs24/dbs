package org.dbs.auth.server.clients.players.grpc.convert

import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.protobuf.auth.PlayerLoginRequest as H2IN
import org.dbs.protobuf.core.JwtsExpiry as H2E
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.rest.dto.value.IssuedJwtResultDto as H1E
import org.dbs.rest.dto.value.LoginUserDto as H1IN
import org.dbs.rest.dto.value.LoginUserResponse as H1OUT

@JvmInline
value class PlayerLoginConverter(override val entClass: KClass<H2E>) : H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E> {
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E( accessJwt.jwt, accessJwt.expiryAt, refreshJwt.jwt, refreshJwt.expiryAt) }
    override fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .setPlayerLogin(login)
            .setPlayerPassword(password)
            .build()
    }

    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
