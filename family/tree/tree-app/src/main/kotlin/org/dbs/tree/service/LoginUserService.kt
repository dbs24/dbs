package org.dbs.tree.service

import org.dbs.enums.I18NEnum
import org.dbs.security.LoginService
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.spring.core.api.TrackExecutionTime
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginUserService(
    private val passwordEncoder: PasswordEncoder,
) : LoginService {

    private val userService by lazy { findService(UserService::class) }

    override suspend fun login(user: String, password: String?): Collection<ErrorInfo> =
        mutableListOf<ErrorInfo>().also { errors ->

            userService.findUserByLogin(user)?.apply user@ {

                if (status != ES_USER_ACTUAL) {
                    errors.add(
                        create(
                            Error.INVALID_ENTITY_STATUS, Field.SSS_USER_STATUS,
                            findI18nMessage(I18NEnum.INVALID_ENTITY_STATUS, status.entityStatusName)
                        )
                    )
                } else
                    password?.apply {
                        if (!passwordEncoder.matches(this, this@user.password)) {
                            errors.add(
                                create(
                                    Error.INVALID_ENTITY_PASSWORD, Field.SSS_USER_PASSWORD,
                                    findI18nMessage(I18NEnum.FLD_INVALID_USER_PASSWORD)
                                )
                            )
                        }
                    }

            } ?: errors.add(
                create(
                    Error.ENTITY_NOT_FOUND, SSS_USER_LOGIN,
                    findI18nMessage(I18NEnum.ENTITY_NOT_FOUND_WITH_ID, user)
                )
            )
        }
}
