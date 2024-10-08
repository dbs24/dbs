package org.dbs.goods.service.grpc.h1.convert

import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.goods.client.UserCredentials as H2E
import org.dbs.goods.client.UserCredentialsRequest as H2IN
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.goods.UserLogin as H1IN
import org.dbs.goods.dto.user.GetUserCredentialsResponse as H1OUT
import org.dbs.goods.dto.user.UserAuthDto as H1E

@JvmInline
value class UserCredentialsConverter(override val entClass: KClass<H2E>) : H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E> {
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(userLogin, userPassword, userStatus) }
    override fun buildRequestH2(h1: H1IN): H2IN = H2IN.newBuilder().setUserLogin(h1).build()
    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
