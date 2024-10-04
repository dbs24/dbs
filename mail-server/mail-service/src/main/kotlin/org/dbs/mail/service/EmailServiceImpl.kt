package org.dbs.mail.service

import jakarta.mail.internet.MimeMessage
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_FROM
import org.dbs.consts.SpringCoreConst.PropertiesNames.SPRING_MAIL_RETRY_ATTEMPT_LIMIT
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.safe.school.mail.dto.NotificationDto
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.I18NService.Companion.getLocaleOrSystemLocale
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailServiceImpl(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) : AbstractApplicationService(), EmailServiceInternal {

    @Value("\${$SPRING_MAIL_FROM}")
    private val sendFrom: String = EMPTY_STRING

    @Value("\${$SPRING_MAIL_RETRY_ATTEMPT_LIMIT:3}")
    private val retryAttemptLimit: Int = 3

    override fun send(notification: NotificationDto) {
        val retryHolder = RetryDataHolder()
        while (retryHolder.isRetryLimitNotReachedAndEmailNotSent(retryAttemptLimit)) {
            sendRetrying(notification, retryHolder)
        }
        if (retryHolder.isSent) {
            logger.debug("Successfully requested to sent email via smtp. $notification")
        }
    }

    private fun sendRetrying(notification: NotificationDto, retryHolder: RetryDataHolder) {
        runCatching {
            javaMailSender.send(*getMimeMessageForEachEmail(notification).toTypedArray())
            retryHolder.isSent = true
        }.onFailure {
            retryHolder.retryAttempts++
            logger.error(getMailSendErrorMessage(notification, retryHolder.retryAttempts), it)
        }
    }

    private fun getMimeMessageForEachEmail(notification: NotificationDto): List<MimeMessage> = run {
        val htmlBody = getHtmlBody(notification)
        notification.sendTo.map { email ->
            getMimeMessage(
                email,
                notification.notificationType.subject,
                notification.subjectParams,
                htmlBody
            )
        }
    }

    private fun getMimeMessage(
        email: String,
        subject: String,
        subjectParams: Set<String>,
        htmlBody: String
    ) =
        javaMailSender.createMimeMessage().also { mimeMessage ->
            MimeMessageHelper(mimeMessage, true).apply {
                setFrom(sendFrom)
                setTo(email)
                setSubject(
                    findI18nMessage(
                        subject,
                        *subjectParams.toTypedArray()
                    )
                )
                setText(htmlBody, true)
            }
        }

    private fun getHtmlBody(notification: NotificationDto) = Context(
        getLocaleOrSystemLocale(notification.locale)
    ).run {
        setVariables(notification.params)
        templateEngine.process(notification.notificationType.template, this)
    }

    private fun getMailSendErrorMessage(notification: NotificationDto, retryAttempts: Int) =
        "Error sending email for notification [$notification]. Count invalid attempts: $retryAttempts"

    private data class RetryDataHolder(
        var retryAttempts: Int = 0,
        var isSent: Boolean = false
    ) {
        fun isRetryLimitNotReachedAndEmailNotSent(retryAttemptLimit: Int) =
            retryAttempts < retryAttemptLimit && !isSent
    }
}
