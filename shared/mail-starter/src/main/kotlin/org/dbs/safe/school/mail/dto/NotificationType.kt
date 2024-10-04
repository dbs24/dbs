package org.dbs.safe.school.mail.dto

enum class NotificationType(
    val template: String,
    val subject: String,
) {
    REGISTRATION("registration", "mail.subject.registration"),
    MANAGER_CHANGE("manager-change", "mail.subject.manager_change"),
    CONFIRM_EMAIL("confirm-email", "mail.subject.confirm_email"),
    CHANGE_PASSWORD("change-password", "mail.subject.change_password"),
    RESET_PASSWORD("reset-password", "mail.subject.reset_password"),
    CREATE_APPLICATION_DISPUTE("create-application-dispute", "mail.subject.dispute_create"),
    APPLICATION_DISPUTE_IS_REJECTED("rejected-application-dispute", "mail.subject.dispute_reject"),
    APPLICATION_DISPUTE_IS_APPROVED("approved-application-dispute", "mail.subject.dispute_approve"),

    // region School
    SCHOOL_REGISTER("school/registration_template", "mail.subject.school.registration_complete"),
    SCHOOL_CHANGE_PASSWORD("school/password_new_template", "mail.subject.school.password_update"),
    SCHOOL_RESET_PASSWORD("school/password_forgot_template", "mail.subject.school.password_forgot"),
    SCHOOL_CHANGE_EMAIL("school/change_email_template", "mail.subject.school.change_email"),
    SCHOOL_ERROR("school/error_msg_template", "mail.subject.school.mail_not_delivered")
    // endregion
}
