package org.dbs.tree.validator.strategy

import org.dbs.rest.api.validator.ValidationStrategy
import org.dbs.tree.service.UserService
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.springframework.stereotype.Component
import org.dbs.user.dto.user.CreateOrUpdateUserDto as DTO

@Component
class UserValidationStrategy(private val userService: UserService) : ValidationStrategy<DTO> {
    override val supportedClass = DTO::class

    override suspend fun validate(request: DTO) {
        validateInternal { errors ->
            if (request.login.isEmpty()) errors.add(create(Error.INVALID_DTO_ATTR, Field.SSS_LOGIN_USER, "errorMsg: String"))
        }
    }
}
