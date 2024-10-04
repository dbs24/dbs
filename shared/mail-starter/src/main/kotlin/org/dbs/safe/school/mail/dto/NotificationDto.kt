package org.dbs.safe.school.mail.dto

import org.dbs.kafka.api.KafkaDocument

data class NotificationDto(
    val sendTo: Set<String>,
    val notificationType: NotificationType,
    val params: Map<String, Any> = mapOf(),
    val subjectParams: Set<String> = setOf(),
    val locale: String? = null // format: en_CA
) : KafkaDocument {

    constructor(
        sendTo: String,
        notificationType: NotificationType,
        params: Map<String, Any>,
        subjectParams: Set<String> = setOf()
    ) : this(setOf(sendTo), notificationType, params, subjectParams)
}
