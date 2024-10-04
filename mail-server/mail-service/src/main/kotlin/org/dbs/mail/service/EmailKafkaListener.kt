package org.dbs.mail.service

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_PROCESS_BATCH_INTERVAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_PROCESS_BATCH_LIMIT
import org.dbs.ext.CoroutineFuncs.isReadyToReceive
import org.dbs.kafka.consts.KafkaConsts.Topics.EMAIL_TOPIC
import org.dbs.safe.school.mail.dto.NotificationDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Lazy(false)
@Service
class EmailKafkaListener(private val emailService: EmailServiceInternal) : AbstractApplicationService() {
    private val notificationChannel by lazy { Channel<NotificationDto>(Channel.UNLIMITED) }

    @Value("\${$SPRING_MAIL_PROCESS_BATCH_LIMIT:10}")
    private val processBatchLimit: Int = 10

    @KafkaListener(topics = [EMAIL_TOPIC])
    fun readNotificationAndSendEmail(notifications: Collection<NotificationDto>) {
        logger.debug("Read email notifications (size=${notifications.size}) from topic: $notifications")
        runBlocking {
            notifications.forEach { notificationChannel.send(it) }
        }
    }

    @Scheduled(
        initialDelayString = "\${$SPRING_MAIL_PROCESS_BATCH_INTERVAL:10}",
        fixedRateString = "\${$SPRING_MAIL_PROCESS_BATCH_INTERVAL:10}",
        timeUnit = TimeUnit.SECONDS
    )
    fun insertFromKafka() = runBlocking {
        val notificationsToProcess = mutableListOf<NotificationDto>()
        while (notificationChannel.isReadyToReceive() && notificationsToProcess.size < processBatchLimit) {
            notificationsToProcess.add(notificationChannel.receive())
        }
        if (notificationsToProcess.isNotEmpty()) {
            logNotificationsToProcess(notificationsToProcess)
        }
        notificationsToProcess.forEach { emailService.send(it) }
    }

    private fun logNotificationsToProcess(notificationsToProcess: MutableList<NotificationDto>) {
        logger.debug(
            "Notifications to process (size=${notificationsToProcess.size}) " +
                "by scheduler: $notificationsToProcess"
        )
    }
}
