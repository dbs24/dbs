package org.dbs.auth.server.service.impl

import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.api.RevokedJwtDto
import org.dbs.auth.server.dao.AuthServerDao
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.model.AbstractJwt
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.AuthServiceLayer.Companion.kafkaService
import org.dbs.auth.server.service.JwtStorageService
import org.dbs.consts.*
import org.dbs.consts.SysConst.LOCALDATETIME_NULL
import org.dbs.kafka.consts.KafkaTopicEnum.REVOKED_JWT
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Error.INVALID_JWT
import org.dbs.validator.Field
import org.dbs.validator.Field.FLD_ACCESS_JWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import kotlin.reflect.KClass

@Service
class JwtStorageServiceImpl(
    val authServerDao: AuthServerDao,
    private val databaseClient: DatabaseClient
) : JwtStorageService, AbstractApplicationService() {

    @Value("\${spring.r2dbc.next-token-cmd:SELECT nextval('seq_tnk_card_id')}")
    private val nextTokenCmd = "SELECT null"

    override fun <T : AbstractJwt> generateNewJwtId(entClass: KClass<T>): Mono<EntityId> =
        databaseClient.sql(nextTokenCmd)
            .map { row -> row.get(0, java.lang.Long::class.java) } //
            .one()
            .map { newId ->
                logger.debug("generate new jwtId: $newId [${entClass.qualifiedName}]")
                newId.toLong()
            }

    override final inline fun findJwt(jwt: Jwt): Mono<IssuedJwt> = authServerDao.findAccessToken(jwt)

    override final inline fun findRefreshJwt(refreshJwt: Jwt): Mono<RefreshJwt>  = authServerDao.findRefreshToken(refreshJwt)

    override final inline fun <AE : AbstractJwt> findJwt(
        response: RAB,
        func: NoArg2Mono<AE>,
        jwtNotFoundMessage: String,
        field: Field,
        crossinline successFunc: GenericArg2Unit<AE>,
    ): Mono<AE> = func()
        .subscribeOn(parallelScheduler)
        .map { successFunc(it); it }
        .switchIfEmpty {
            response.addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_ENTITY_ATTR, field, jwtNotFoundMessage
            )
            empty()
        }

    override fun findActualJwt(
        response: RAB,
        accessJwt: Jwt,
    ): Mono<IssuedJwt> = accessJwt.run {
        findJwt(
            response,
            { authServerDao.findActualToken(accessJwt) },
            "unknown expired token ($accessJwt)",
            FLD_ACCESS_JWT,
            {
                logger.debug("found expired token: ${it.jwt.last15()} ")
                if (it.isRevoked) {
                    response.addErrorInfo(
                        RC_INVALID_REQUEST_DATA,
                        INVALID_JWT, FLD_ACCESS_JWT, "access jwt was revoked: ${it.jwt.last15()}"
                    )
                }
            })
    }

    override fun findRefreshJwt(
        response: RAB,
        refreshJwt: Jwt,
    ): Mono<RefreshJwt> = refreshJwt.run {
        findJwt(
            response,
            { authServerDao.findRefreshToken(refreshJwt) },
            "unknown access token ($refreshJwt)",
            FLD_ACCESS_JWT,
            {
                logger.debug("found refresh token: ${it.jwt.last15()} ")
                if (it.isRevoked) {
                    response.addErrorInfo(
                        RC_INVALID_REQUEST_DATA,
                        INVALID_JWT, FLD_ACCESS_JWT, "refresh jwt was revoked: ${it.jwt.last15()}"
                    )
                }
            })
    }

    override final inline fun createAndSaveAccessJwt(
        tokenKey: String,
        validUntil: LocalDateTime,
        application: ApplicationEnum,
        jwtOwner: String,
        crossinline tokenBuilder: NoArg2String
    ): Mono<IssuedJwt> = generateNewJwtId(IssuedJwt::class)
        .flatMap { newEntityId ->

            // remove old issued tokens
            logger.debug("Revoke previous tokens for '$jwtOwner' (application=$application)")
            authServerDao.revokeExistsJwt(jwtOwner, application)
                .then(authServerDao.saveIssuedToken(
                    IssuedJwt(
                        jwtId = newEntityId,
                        applicationId = application,
                        issueDate = now(),
                        validUntil = validUntil,
                        jwt = tokenBuilder(),
                        issuedTo = jwtOwner,
                        tag = tokenKey,
                        isRevoked = false,
                        revokeDate = LOCALDATETIME_NULL
                    ).asNew<IssuedJwt>()
                        .also {
                            logger.debug("issue new access jwt for '$jwtOwner' ($tokenKey), application=$application")
                        }
                ))
        }

    override final inline fun createAndSaveRefreshJwt(
        parentJwtId: JwtId,
        validUntil: LocalDateTime,
        tokenKey: String,
        crossinline tokenBuilder: NoArg2String
    ): Mono<RefreshJwt> = generateNewJwtId(RefreshJwt::class)
        .flatMap { newEntityId ->
            logger.info("issue new refresh token for '$tokenKey'")
            authServerDao.saveRefreshToken(
                RefreshJwt(
                    jwtId = newEntityId,
                    issueDate = now(),
                    validUntil = validUntil,
                    jwt = tokenBuilder(),
                    parentJwtId = parentJwtId,
                    isRevoked = false,
                    revokeDate = LOCALDATETIME_NULL
                ).asNew()
            )
        }

    override fun revokeExistsJwt(jwtOwner: String, application: ApplicationEnum) =
        authServerDao.revokeExistsJwt(jwtOwner, application)
            .map { listOf<String>() } // faked map that change return type
            .switchIfEmpty {
                // notify auth verify service about revoked jwt
                logger.info("findRevokedJwt 4 $jwtOwner")
                findRevokedJwt(jwtOwner, application).collectList()
            }.map {
                it.forEach {
                    kafkaService.send(REVOKED_JWT, RevokedJwtDto(it.last15().substring(4)))
                }
            }
            .then()


    override fun findRevokedJwt(jwtOwner: String, application: ApplicationEnum): Flux<Jwt> =
        authServerDao.findRevokedTokens(jwtOwner, application)

    override fun deleteDeprecatedJwt(deprecateDate: OperDate) = authServerDao.deleteDeprecatedJwt(deprecateDate)
    override fun arcDeprecatedJwt(deprecateDate: OperDate) = authServerDao.arcDeprecatedJwt(deprecateDate)

    override fun moveJwt2Arc(expiredIssuedJwt: IssuedJwt) = authServerDao.moveJwt2Arc(expiredIssuedJwt)
    override fun moveRefreshJwt2Arc(obsoleteRefreshJwt: RefreshJwt) = authServerDao.moveRefreshJwt2Arc(obsoleteRefreshJwt)

}
