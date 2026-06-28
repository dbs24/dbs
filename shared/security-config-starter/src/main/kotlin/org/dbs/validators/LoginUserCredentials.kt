package org.dbs.validators

import org.dbs.application.core.service.funcs.Patterns
import org.dbs.model.domain.LoginUserCommand as DTO
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.security.LoginService
import org.dbs.validator.Field
import org.springframework.stereotype.Component

@Component
class LoginUserCredentials(
    private val loginService: LoginService,
) : ValidationStrategy<DTO> {

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::login matches (Patterns.LOGIN_PATTERN to Field.SSS_USER_LOGIN),
        DTO::password matches (Patterns.PASSWORD_PATTERN to Field.SSS_USER_PASSWORD),
        DTO::userAgent matches (Patterns.USER_AGENT to Field.SSS_USER_AGENT),
    )

    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            request.apply {
                loginService.login(request.login, request.password).apply {
                            if (isNotEmpty()) errors.addAll(this)
                }
            }
        }
    }
}