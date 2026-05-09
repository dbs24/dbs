package org.dbs.tree.validator.strategy

import org.dbs.enums.I18NEnum
import org.dbs.rest.api.validator.ValidationStrategy
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.tree.service.UserService
import org.dbs.validator.Error.INVALID_DTO_ATTR
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field.SSS_LOGIN_USER
import org.springframework.stereotype.Component
import org.dbs.user.dto.user.CreateOrUpdateUserDto as DTO

@Component
class UserValidationStrategy(private val userService: UserService) : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override fun validate(request: DTO) {
        validateInternal { errors ->

            if (request.login.isEmpty()) errors.add(
                create(INVALID_DTO_ATTR, SSS_LOGIN_USER,
                    findI18nMessage(I18NEnum.FLD_INVALID_VALUE)))

            //errors.add(create(Error.INVALID_DTO_ATTR, Field.SSS_LOGIN_USER, "full error"))

        }
    }
}
