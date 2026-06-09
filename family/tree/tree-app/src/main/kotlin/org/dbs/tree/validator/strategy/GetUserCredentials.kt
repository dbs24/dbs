package org.dbs.tree.validator.strategy

import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.enums.I18NEnum
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.tree.service.UserService
import org.dbs.user.FamilyTreeCore.EntityStatus
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.validator.Field.SSS_USER_STATUS
import org.springframework.stereotype.Component
import org.dbs.tree.model.domain.GetUserCredentialsCommand as DTO

@Component
class GetUserCredentials(
    private val userService: UserService
) : ValidationStrategy<DTO> {

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::login matches (LOGIN_PATTERN to SSS_USER_LOGIN),
    )

    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            request.apply {

                userService.findUserByLogin(login)
                    ?.apply user@{

                        updatedUser = this

                        if (status != EntityStatus.ES_USER_ACTUAL) {
                            errors.add(
                                create(
                                    org.dbs.validator.Error.INVALID_ENTITY_STATUS, SSS_USER_STATUS,
                                    findI18nMessage(I18NEnum.INVALID_ENTITY_STATUS, status.entityStatusName)
                                )
                            )
                        }

                    } ?: run {
                    errors.add(
                        create(
                            Error.USER_DOES_NOT_EXISTS, SSS_USER_LOGIN,
                            findI18nMessage(I18NEnum.FLD_UNKNOWN_USER_LOGIN, login)
                        )
                    )
                }
            }
        }
    }

}
