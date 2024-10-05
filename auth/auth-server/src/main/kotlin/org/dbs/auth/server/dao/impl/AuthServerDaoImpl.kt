package org.dbs.auth.server.dao.impl

import org.dbs.auth.server.dao.AuthServerDao
import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.enums.ApplicationEnum.Companion.isExistEnum
import org.dbs.auth.server.enums.ApplicationEnum.entries
import org.dbs.auth.server.model.*
import org.dbs.auth.server.repo.*
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

@Service
@Lazy(false)
class AuthServerDaoImpl(
    private val issuedJwtRepo: IssuedJwtRepo,
    private val issuedJwtArcRepo: IssuedJwtArcRepo,
    private val refreshJwtRepo: RefreshJwtRepo,
    private val refreshJwtArcRepo: RefreshJwtArcRepo,
    private val applicationRepo: ApplicationRepo,
) : AuthServerDao, DaoAbstractApplicationService() {

    val actualApplications: Collection<Application> by lazy {
        entries.map { Application(it.getApplicationId(), it.name, it.getApplicationName()) }
            .toList()
    }

    override fun synchronizeRefs() = measureTimeMillis {
        fromIterable(actualApplications)
            .publishOn(parallelScheduler)
            .noDuplicates({ it.applicationId }, { it.applicationCode })
            .synchronizeReference(applicationRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })
        applicationRepo.findAll().validateDb { isExistEnum(it.id) }.count().subscribeMono()
    }.apply { logger.debug { "synchronizeRefs: took $this ms" } }

    override fun moveJwt2Arc(expiredIssuedJwt: IssuedJwt): Mono<IssuedJwtArc> =
        expiredIssuedJwt.run {
            createIssuedArcToken(
                IssuedJwtArc(
                    jwtId = jwtId,
                    issueDate = issueDate,
                    validUntil = validUntil,
                    jwt = jwt,
                    applicationId = applicationId,
                    issuedTo = issuedTo,
                    tag = tag,
                    isRevoked = isRevoked,
                    revokeDate = revokeDate
                ).asNew()
            )
        }

    override fun moveRefreshJwt2Arc(obsoleteRefreshJwt: RefreshJwt) =
        obsoleteRefreshJwt.run {
            createRefreshArcToken(
                RefreshJwtArc(
                    jwtId = jwtId,
                    issueDate = issueDate,
                    validUntil = validUntil,
                    jwt = jwt,
                    refreshDate = LocalDateTime.now(),
                    parentJwtId = parentJwtId,
                    isRevoked = isRevoked,
                    revokeDate = revokeDate
                ).asNew()
            ).flatMap { deleteDeprecatedJwt(jwtId, parentJwtId) }
        }

    override fun deleteDeprecatedJwt(obsoleteRefreshJwtId: JwtId, expiredIssueJwtId: JwtId): Mono<Void> = run {
        deleteRefreshToken(obsoleteRefreshJwtId)
            .thenEmpty(deleteIssuedToken(expiredIssueJwtId))
    }

    override fun saveIssuedToken(issuedJwt: IssuedJwt): Mono<IssuedJwt> = issuedJwtRepo.save(issuedJwt)
    override fun deleteIssuedToken(jwtId: JwtId): Mono<Void> = issuedJwtRepo.deleteById(jwtId)
    override fun createIssuedArcToken(issuedJwtArc: IssuedJwtArc): Mono<IssuedJwtArc> = issuedJwtArcRepo.save(issuedJwtArc)
    override fun saveRefreshToken(refreshJwt: RefreshJwt): Mono<RefreshJwt> = refreshJwtRepo.save(refreshJwt)
    override fun deleteRefreshToken(jwtId: JwtId): Mono<Void> = refreshJwtRepo.deleteById(jwtId)
    override fun createRefreshArcToken(refreshJwtArc: RefreshJwtArc): Mono<RefreshJwtArc> = refreshJwtArcRepo.save(refreshJwtArc)
    override fun findAccessToken(jwt: Jwt): Mono<IssuedJwt> = issuedJwtRepo.findAccessJwt(jwt)
    override fun findActualToken(jwt: Jwt): Mono<IssuedJwt> = issuedJwtRepo.findActualJwtUnique(jwt)
    override fun findRefreshToken(jwt: Jwt): Mono<RefreshJwt> = refreshJwtRepo.findByJwtUnique(jwt)
    override fun deleteDeprecatedJwt(deprecateDate: OperDate) = issuedJwtRepo.deleteDeprecatedJwt(deprecateDate)
    override fun arcDeprecatedJwt(deprecateDate: OperDate) = issuedJwtRepo.arcDeprecatedJwt(deprecateDate)
    override fun revokeExistsJwt(jwtOwner: String, application: ApplicationEnum) = issuedJwtRepo.revokeJwt(jwtOwner, application)
    override fun findRevokedTokens(jwtOwner: String, application: ApplicationEnum): Flux<Jwt>  = issuedJwtRepo.findRevokedJwt(jwtOwner, application)

    override fun initialize() = super.initialize().also { synchronizeRefs() }
}
