package org.dbs.kafka.config

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_ENABLED
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.kafka.consts.KafkaConsts.TP_FAKED
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.ProducerListener
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter
import org.springframework.kafka.support.converter.RecordMessageConverter

@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@ConditionalOnProperty(KAFKA_ENABLED, havingValue = STRING_TRUE, matchIfMissing = true)
class KafkaConfig {

    @Bean
    fun kafkaTemplate(
        kafkaProducerFactory: ProducerFactory<Any, Any>,
        kafkaProducerListener: ProducerListener<Any, Any>,
        messageConverter: ObjectProvider<RecordMessageConverter>,
    ): KafkaTemplate<Any, Any> = KafkaTemplate(kafkaProducerFactory).apply {
        messageConverter.ifUnique { setMessageConverter(it) }
        setProducerListener(kafkaProducerListener)
        defaultTopic = TP_FAKED
    }

    @Bean
    fun batchConverter(rmc: RecordMessageConverter) = BatchMessagingMessageConverter(rmc)

    @Bean
    fun converter(): RecordMessageConverter = ByteArrayJsonMessageConverter()

}
