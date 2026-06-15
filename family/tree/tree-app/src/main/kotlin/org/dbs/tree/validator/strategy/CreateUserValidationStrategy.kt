package org.dbs.tree.validator.strategy

import org.dbs.enums.I18NEnum
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.tree.service.UserService
import org.dbs.validator.Error
import org.dbs.validator.Error.ALREADY_EXISTS
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.springframework.stereotype.Component
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand as DTO

@Component
class CreateUserValidationStrategy(
    private val userService: UserService,
) : ValidationStrategy<DTO>, UserValidationPattern {

    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            request.apply {

                val user = userService.findUserByLogin(login)

                // new login should not exist
                if (isNewUser || isUpdateLogin) {

                    user?.apply {  errors.add(
                                create(
                                    ALREADY_EXISTS, SSS_USER_LOGIN,
                                    findI18nMessage(I18NEnum.EXIST_USER_LOGIN)
                                ))   }
                } else {

                    user?.apply {
                        request.updatedUser = this
                    } ?:
                        errors.add(
                            create(
                                Error.ENTITY_NOT_FOUND, SSS_USER_LOGIN,
                                findI18nMessage(I18NEnum.ENTITY_NOT_FOUND_WITH_ID, login)
                            ))
                }
            }
        }
    }
}
