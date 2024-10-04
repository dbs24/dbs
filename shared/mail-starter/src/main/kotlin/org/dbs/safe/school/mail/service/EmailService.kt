package org.dbs.safe.school.mail.service

import org.dbs.consts.RestHttpConsts.URI_LOCALHOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_HOST_KEY
import org.dbs.consts.SpringCoreConst.PropertiesNames.MAIL_PORT_KEY
import org.dbs.consts.SysConst.INTEGER_ZERO
import org.dbs.kafka.consts.KafkaTopicEnum.EMAIL
import org.dbs.kafka.service.KafkaService
import org.dbs.safe.school.mail.dto.NotificationDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class EmailService(private val kafkaService: KafkaService) : AbstractApplicationService() {

    @Value("\${$MAIL_HOST_KEY:$URI_LOCALHOST}")
    private val mailServerHost = URI_LOCALHOST

    @Value("\${$MAIL_PORT_KEY:$INTEGER_ZERO}")
    private val mailServerPort = INTEGER_ZERO

    fun sendEmail(notification: NotificationDto) = notification.apply {
        logger.debug { "send notification: $this" }
        kafkaService.send(EMAIL, this)
    }
    override fun initialize() = super.initialize()
        .also { addHost4LivenessTracking(mailServerHost, mailServerPort, javaClass.simpleName) }

}
