package org.dbs.validators

import org.dbs.application.core.service.funcs.Patterns
import org.dbs.model.domain.LoginUserCommand
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.security.LoginService
import org.dbs.validator.Field
import org.springframework.stereotype.Component

@Component
class LoginUserCredentials(
    private val loginService: LoginService,
) : ValidationStrategy<LoginUserCommand> {

    override val rules: Collection<FieldValidationRule<LoginUserCommand>> = listOf(
        LoginUserCommand::login matches (Patterns.LOGIN_PATTERN to Field.SSS_USER_LOGIN),
        LoginUserCommand::password matches (Patterns.PASSWORD_PATTERN to Field.SSS_USER_PASSWORD),
    )

    override fun validate(request: LoginUserCommand) {
        validateInternal(request) { errors ->

            request.apply {
                loginService.login(request.login, request.password).apply {
                            if (isNotEmpty()) errors.addAll(this)
                }
            }
        }
    }
}