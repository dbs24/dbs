package org.dbs.auth.server.service

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toString2
import org.dbs.auth.server.api.ApplicationLogin4RevokeDto
import org.dbs.auth.server.service.impl.JwtStorageServiceImpl
import org.dbs.ext.CoroutineFuncs.isReadyToReceive
import org.dbs.kafka.consts.KafkaConsts.Groups.MANAGER_GROUP_ID
import org.dbs.kafka.consts.KafkaConsts.Topics.KAFKA_MANAGER_LOGIN
import org.dbs.spring.core.api.ScheduledAbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.util.concurrent.TimeUnit.SECONDS

@Lazy(false)
@Service
class JwtScheduledService(
    private val jwtStorageServiceImpl: JwtStorageServiceImpl,
) : ScheduledAbstractApplicationService() {

    @Value("\${config.security.jwt.delete-deprecated.expiryDays:60}")
    val expiryDays = 60L // days

    @Value("\${config.security.jwt.delete-deprecated.period:3600}")
    val refreshPeriod = 3600L // seconds

    private val channelManagersList by lazy { Channel<Collection<ApplicationLogin4RevokeDto>>(UNLIMITED) }

    @KafkaListener(id = KAFKA_MANAGER_LOGIN, groupId = MANAGER_GROUP_ID, topics = [KAFKA_MANAGER_LOGIN])
    fun receiveRequests(managers: Collection<ApplicationLogin4RevokeDto>) = managers.apply {
        logger.info("receive managers list (${managers.size}: [$managers]")
        runBlocking { channelManagersList.send(managers) }
    }

    @Scheduled(
        initialDelayString = "5",
        fixedRateString = "\${config.security.jwt.process-revoked.period:1}",
        timeUnit = SECONDS
    )
    fun getLoginsFromChannel(): Unit =
        if (channelManagersList.isReadyToReceive()) runBlocking {
            channelManagersList.receive().let { logins ->
                logger.debug("revoke jwt 4 login: $logins")
                logins.forEach { jwtStorageServiceImpl.revokeExistsJwt(it.login, it.application).subscribe() }
            }
        }
        else {
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Manage deprecated JWT
    @Scheduled(
        initialDelayString = "5",
        fixedRateString = "\${config.security.jwt.delete-deprecated.period:60}",
        timeUnit = SECONDS
    )
    fun scheduledProcedure() = runBlocking { processDeprecatedJwt() }

    suspend fun processDeprecatedJwt() = coroutineScope {
        launch {

            val now = now()
            val nextRun = "next run: ${now.plusSeconds(refreshPeriod).toString2()}"

            jwtStorageServiceImpl.arcDeprecatedJwt(now.also { logger.debug { "send deprecated jwt 2 arc (issued before  ${it.toString2()}); $nextRun" } })
                .then(
                    jwtStorageServiceImpl.deleteDeprecatedJwt(
                        now.minusDays(expiryDays)
                            .also { logger.debug { "delete deprecated jwt (issued before ${it.toString2()}, $expiryDays day(s); $nextRun" } })
                )
                .subscribe()
        }
    }

    override fun initialize() = super.initialize().also {
        logger.debug("Jwt storage expiryDays: $expiryDays days, refreshPeriod: $refreshPeriod seconds")
    }
}
