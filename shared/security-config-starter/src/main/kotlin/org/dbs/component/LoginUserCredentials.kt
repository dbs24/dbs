package org.dbs.component

import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.application.core.service.funcs.Patterns.PASSWORD_PATTERN
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.security.LoginService
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.validator.Field.SSS_USER_PASSWORD
import org.springframework.stereotype.Component
import org.dbs.model.domain.LoginUserCommand as DTO

@Component
class LoginUserCredentials(
    private val loginService: LoginService,
) : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::login matches (LOGIN_PATTERN to SSS_USER_LOGIN),
        DTO::password matches (PASSWORD_PATTERN to SSS_USER_PASSWORD),
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
