package org.dbs.tree.validator.strategy

import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PHONE_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_FIRST_NAME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_LAST_NAME_PATTERN
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationPattern
import org.dbs.validator.Field
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.validator.Field.SSS_USER_OLD_LOGIN
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand as DTO

interface UserValidationPattern : ValidationPattern<DTO> {

    override val rules: Collection<FieldValidationRule<DTO>>
        get() = listOf(
            DTO::oldLogin matches (LOGIN_PATTERN to SSS_USER_OLD_LOGIN),
            DTO::login matches (LOGIN_PATTERN to SSS_USER_LOGIN),
            DTO::oldEmail matches (EMAIL_PATTERN to Field.SSS_USER_EMAIL),
            DTO::email matches (EMAIL_PATTERN to Field.SSS_USER_EMAIL),
            DTO::phone matches (PHONE_PATTERN to Field.SSS_USER_PHONE),
            DTO::password matches (PASSWORD_PATTERN to Field.SSS_USER_PASSWORD),
            DTO::firstName matches (USER_FIRST_NAME_PATTERN to Field.SSS_USER_FIRST_NAME),
            DTO::lastName matches (USER_LAST_NAME_PATTERN to Field.SSS_USER_LAST_NAME),
            DTO::middleName matches (USER_LAST_NAME_PATTERN to Field.SSS_USER_MIDDLE_NAME),
        )
}
