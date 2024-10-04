package org.dbs.kafka.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_ENABLED
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.kafka.api.KafkaDocument
import org.dbs.kafka.consts.KafkaTopicEnum
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
@Lazy(false)
@ConditionalOnProperty(KAFKA_ENABLED, havingValue = STRING_TRUE, matchIfMissing = true)
class KafkaService(private val producer: KafkaTemplate<Any, Any>) : AbstractKafkaBrokerService() {
    fun <T : KafkaDocument> send(topic: KafkaTopicEnum, item: T) = item.apply {
        logger.debug { "==> [kafka, topic['$topic']: [$this]" }
        producer.send(topic.topic, this)
    }
}
