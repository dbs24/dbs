package org.dbs.mail.service

import org.dbs.safe.school.mail.dto.NotificationDto


interface EmailServiceInternal {
    fun send(notification: NotificationDto)
}
