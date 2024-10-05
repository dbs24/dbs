package org.dbs.auth.verify.service.grpc

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import net.devh.boot.grpc.server.service.GrpcService
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.auth.server.api.RevokedJwtDto
import org.dbs.auth.verify.clients.auth.api.JwtAttrs
import org.dbs.auth.verify.clients.auth.grpc.GrpcJwtVerify.verifyJwtInternal
import org.dbs.auth.verify.consts.AuthVerifyConsts.CK_VERIFY_JWT_PROCEDURE
import org.dbs.auth.verify.service.AuthServiceLayer.Companion.jwtVerifyGrpcService
import org.dbs.consts.Jwt
import org.dbs.kafka.consts.KafkaConsts.Groups.AUTH_SERVICE_GROUP_ID
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_REVOKE_JWT_TOPIC
import org.dbs.protobuf.auth.AuthVerifyClientServiceGrpcKt
import org.dbs.protobuf.auth.VerifyJwtRequest
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now

@Service
@Lazy(false)
class JwtVerifyGrpcService : AbstractGrpcServerService(), PublicApplicationBean {

    // white grpc procedures
    override val whiteProcs = setOf(CK_VERIFY_JWT_PROCEDURE)

    val cachedJwtList by lazy { JwtsListWrapperImpl() }

    @GrpcService
    inner class JwtVerifyService : AuthVerifyClientServiceGrpcKt.AuthVerifyClientServiceCoroutineImplBase(),
        PublicApplicationBean {
        override suspend fun verifyJwt(request: VerifyJwtRequest) =
            jwtVerifyGrpcService.verifyJwtInternal(request)

    }

    @KafkaListener(id = KAFKA_REVOKE_JWT_TOPIC, groupId = AUTH_SERVICE_GROUP_ID, topics = [KAFKA_REVOKE_JWT_TOPIC])
    fun receiveRequests(revokedJwt: Collection<RevokedJwtDto>) = revokedJwt.apply {

        val toRevoke = map { it.jwt }

        logger.info("receive jwt list 4 revoke (${revokedJwt.size}: [$revokedJwt]")
        cachedJwtList.removeJwtList(toRevoke)
    }

    //==================================================================================================================
    sealed interface JwtsListWrapper {
        val mutex: Mutex
        val jwtMap: HashMap<Jwt, JwtAttrs>
        suspend fun addJwt(jwt: Jwt, jwtAttrs: JwtAttrs)
        fun findJwt(jwt: Jwt): JwtAttrs?
        suspend fun removeJwt(jwt: Jwt)
        fun removeObsoletedJwt()
        fun removeJwtList(jwt: Collection<Jwt>)
    }

    //==================================================================================================================
    inner class JwtsListWrapperImpl : JwtsListWrapper, Logging {
        override val mutex: Mutex = Mutex()
        override val jwtMap: HashMap<Jwt, JwtAttrs> = HashMap()

        override suspend fun addJwt(jwt: Jwt, jwtAttrs: JwtAttrs) {
            mutex.apply {
                lock()
                jwtMap[jwt] = jwtAttrs
                unlock()
            }
            removeObsoletedJwt()
        }

        override fun findJwt(jwt: Jwt): JwtAttrs? = jwtMap[jwt]

        override suspend fun removeJwt(jwt: Jwt) {
            mutex.apply {
                lock()
                jwtMap.remove(jwt)
                unlock()
            }

            removeObsoletedJwt()
        }

        override fun removeJwtList(jwt: Collection<Jwt>): Unit = runBlocking {
            mutex.apply {
                lock()
                logger.debug { "removeJwtList: [$jwt]" }
                jwt.forEach {
                    jwtMap.remove(it)
                }
                logger.debug { "actual jwtMap: [$jwtMap]" }
                unlock()
            }
        }

        override fun removeObsoletedJwt(): Unit = runBlocking {
            mutex.apply {
                lock()
                val now = now()
                jwtMap.filter { it.value.validUntil < now }
                    .also {
                        if (it.isNotEmpty()) {
                            logger.debug { "remove from cache: $it" }
                            it.forEach { jwtMap.remove(it.key) }
                            logger.debug { "after remove from cache: $jwtMap" }
                        }
                    }
                unlock()
            }
        }
    }
}