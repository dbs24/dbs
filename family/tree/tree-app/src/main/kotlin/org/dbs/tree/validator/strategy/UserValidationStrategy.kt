package org.dbs.tree.validator.strategy

import kotlinx.coroutines.runBlocking
import org.dbs.enums.I18NEnum
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.tree.service.UserService
import org.dbs.validator.Error.ALREADY_EXISTS
import org.dbs.validator.Error.INVALID_DTO_ATTR
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field.SSS_LOGIN_USER
import org.springframework.stereotype.Component
import org.dbs.tree.model.domain.CreateOrUpdateUserCommand as DTO

@Component
class UserValidationStrategy(private val userService: UserService) : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override fun validate(request: DTO) {
        validateInternal { errors ->

            request.apply {

                if (login.isEmpty()) errors.add(
                    create(
                        INVALID_DTO_ATTR, SSS_LOGIN_USER,
                        findI18nMessage(I18NEnum.FLD_INVALID_VALUE)
                    )
                )

                // new login should not exist
                if (isNewUser || isUpdateLogin) {

                    runBlocking {

                        userService.findUserByLogin(login)
                            ?.apply {  errors.add(
                                create(
                                    ALREADY_EXISTS, SSS_LOGIN_USER,
                                    findI18nMessage(I18NEnum.EXIST_USER_LOGIN)
                                ))   }
                    }
                }
            }
        }
    }
}
