package org.dbs.goods.service.grpc.h1.convert

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import kotlin.reflect.KClass
import org.dbs.goods.client.CreateOrUpdateUserRequest as H2IN
import org.dbs.protobuf.core.MainResponse as H2OUT
import org.dbs.goods.client.CreatedUserDto as H2E
import org.dbs.goods.dto.user.CreateOrUpdateUserDto as H1IN
import org.dbs.goods.dto.user.CreateUserResponse as H1OUT
import org.dbs.goods.dto.user.CreatedUserDto as H1E

@JvmInline
value class CreateOrUpdateUserConverter(override val entClass: KClass<H2E>) :
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
            .setMiddleName(middleName)
            .let { birthDate?.apply { it.setBirthDate(birthDate ?: 0) }; it}
            .let { country?.apply { it.setCountry(country) }; it}
            .let { gender?.apply { it.setGender(gender) }; it}
            .let { avatar?.apply { it.setAvatar(avatar) }; it}
            .let { smallAvatar?.apply { it.setSmallAvatar(smallAvatar) }; it}
            .build()
    }
    override fun buildEntityH1(h2: H2E): H1E = h2.run { H1E(userLogin, email, status) }
    override fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
