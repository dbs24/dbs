package org.dbs.safe.school.mail.config

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.kafka.consts.KafkaConsts.PARTITIONS_DEF
import org.dbs.kafka.consts.KafkaTopicEnum.EMAIL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin.NewTopics

@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
class KafkaMailConfig {

    @Bean
    fun emailServerTopicBuilder(): NewTopics = NewTopics(
        TopicBuilder.name(EMAIL.topic)
            .partitions(PARTITIONS_DEF)
            .build()
    )
}
