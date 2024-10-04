package org.dbs.auth.server.service

import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.model.AbstractJwt
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.IssuedJwtArc
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.consts.*
import org.dbs.service.RAB
import org.dbs.spring.core.api.ServiceBean
import org.dbs.validator.Field
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import kotlin.reflect.KClass

interface JwtStorageService : ServiceBean {

    fun <T : AbstractJwt> generateNewJwtId(entClass: KClass<T>): Mono<EntityId>

    fun findJwt(jwt: Jwt): Mono<IssuedJwt>

    fun findRefreshJwt(refreshJwt: Jwt): Mono<RefreshJwt>

    fun <AE : AbstractJwt> findJwt(
        response: RAB,
        func: NoArg2Mono<AE>,
        jwtNotFoundMessage: String,
        field: Field,
        successFunc: GenericArg2Unit<AE>,
    ): Mono<AE>

    fun findActualJwt(
        response: RAB,
        expiredJwt: Jwt,
    ): Mono<IssuedJwt>

    fun findRefreshJwt(
        response: RAB,
        refreshJwt: Jwt,
    ): Mono<RefreshJwt>


    fun createAndSaveAccessJwt(
        tokenKey: String,
        validUntil: LocalDateTime,
        application: ApplicationEnum,
        jwtOwner: String,
        tokenBuilder: NoArg2String
    ): Mono<IssuedJwt>

    fun createAndSaveRefreshJwt(
        parentJwtId: JwtId,
        validUntil: LocalDateTime,
        tokenKey: String,
        tokenBuilder: NoArg2String
    ): Mono<RefreshJwt>

    fun revokeExistsJwt(jwtOwner: String, application: ApplicationEnum): Mono<Void>

    fun deleteDeprecatedJwt(deprecateDate: OperDate): Mono<Void>
    fun arcDeprecatedJwt(deprecateDate: OperDate): Mono<Void>

    fun moveJwt2Arc(expiredIssuedJwt: IssuedJwt): Mono<IssuedJwtArc>
    fun moveRefreshJwt2Arc(obsoleteRefreshJwt: RefreshJwt): Mono<Void>

}