package org.dbs.tree.validator.strategy

import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.STATUS_PATTERN
import org.dbs.entity.core.EntityStatusEnum
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
import org.dbs.tree.model.domain.UpdateUserStatusCommand as DTO

@Component
class UpdateUserStatusValidationStrategy(
    private val userService: UserService,
) : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::login matches (LOGIN_PATTERN to SSS_USER_LOGIN),
        DTO::status matches (STATUS_PATTERN to SSS_USER_STATUS),
    )

    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            request.apply {

                userService.findUserByLogin(login)
                    ?.apply user@{

                        EntityStatusEnum.findStatus<EntityStatus>(request.status)?.apply status@{
                            if (this@status == this@user.status) {
                                errors.add(
                                    create(
                                        Error.INVALID_ENTITY_STATUS, SSS_USER_STATUS,
                                        findI18nMessage(I18NEnum.ENTITY_ALREADY_HAS_APPLIED_STATUS, request.status)
                                    )
                                )
                            }

                        } ?: errors.add(
                            create(
                                Error.UNKNOWN_ENTITY_STATUS, SSS_USER_STATUS,
                                findI18nMessage(I18NEnum.UNKNOWN_ENTITY_STATUS, request.status)
                            )
                        )

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
