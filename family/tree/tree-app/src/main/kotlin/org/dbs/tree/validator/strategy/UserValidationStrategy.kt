package org.dbs.tree.validator.strategy

import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PHONE_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_FIRST_NAME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.USER_LAST_NAME_PATTERN
import org.dbs.enums.I18NEnum
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.tree.service.UserService
import org.dbs.validator.Error.ALREADY_EXISTS
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.validator.Field.SSS_USER_OLD_LOGIN
import org.springframework.stereotype.Component
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand as DTO

@Component
class UserValidationStrategy(
    private val userService: UserService,
) : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
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

    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            request.apply {

                // new login should not exist
                if (isNewUser || isUpdateLogin) {

                        userService.findUserByLogin(login)
                            ?.apply {  errors.add(
                                create(
                                    ALREADY_EXISTS, SSS_USER_LOGIN,
                                    findI18nMessage(I18NEnum.EXIST_USER_LOGIN)
                                ))   }
                }
            }
        }
    }
}
