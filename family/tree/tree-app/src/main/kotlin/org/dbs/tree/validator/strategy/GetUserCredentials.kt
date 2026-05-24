package org.dbs.tree.validator.strategy

import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.springframework.stereotype.Component
import org.dbs.tree.model.domain.GetUserCredentialsCommand as DTO

@Component
class GetUserCredentials : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::login matches (LOGIN_PATTERN to SSS_USER_LOGIN),
    )

}
