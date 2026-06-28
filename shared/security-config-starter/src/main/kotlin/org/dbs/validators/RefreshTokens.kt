package org.dbs.validators

import org.dbs.application.core.service.funcs.Patterns
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.consts.SecurityConsts.Claims.CL_USER
import org.dbs.enums.I18NEnum
import org.dbs.repo.AccessJwtRepo
import org.dbs.repo.RefreshJwtRepo
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.security.LoginService
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.spring.security.api.JwtSecurityServiceApi
import org.dbs.validator.Error
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field
import org.springframework.stereotype.Component
import org.dbs.model.domain.RefreshTokensCommand as DTO

@Component
class RefreshTokens(
    private val loginService: LoginService,
    private val accessJwtRepo: AccessJwtRepo,
    private val refreshJwtRepo: RefreshJwtRepo,
    private val jwtSecurityService: JwtSecurityServiceApi
) : ValidationStrategy<DTO> {

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::accessToken matches (Patterns.JWT_PATTERN to Field.FLD_ACCESS_JWT),
        DTO::refreshToken matches (Patterns.JWT_PATTERN to Field.FLD_REFRESH_JWT),
        DTO::userAgent matches (Patterns.USER_AGENT to Field.SSS_USER_AGENT),
    )

    @Suppress("CyclomaticComplexMethod")
    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            // access token
            accessJwtRepo.findByJwt(request.accessToken)?.apply {

                request.issuedJwt = this

                if (this.isRevoked) {
                    errors.add(
                        create(
                            Error.INVALID_JWT, Field.FLD_ACCESS_JWT,
                            findI18nMessage(I18NEnum.FLD_JWT_IS_REVOKED, request.accessToken.last15())
                        )
                    )
                } else {

                    require(this.revokeDate == null) { "Tokens is revoked: ${this.revokeDate}" }
                    // get login from claims
                    jwtSecurityService.getClaimAbs(jwt, CL_USER)?.apply {
                        request.login = this

                        require(this == request.issuedJwt.issuedTo) { "Tokens owners are not linked" }


                    } ?: errors.add(
                        create(
                            Error.CLAIM_NOT_FOUND, Field.JWT_TOKEN_CLAIM,
                            findI18nMessage(I18NEnum.JWT_CLAIM_IS_MISSING, CL_USER, request.accessToken.last15())
                        )
                    )
                }
            } ?: errors.add(
                create(
                    Error.INVALID_JWT, Field.FLD_ACCESS_JWT,
                    findI18nMessage(I18NEnum.FLD_INVALID_JWT, request.accessToken.last15())
                )
            )

            // refresh token
            if (!errors.isNotEmpty())
                refreshJwtRepo.findByJwt(request.refreshToken)?.apply {

                    request.refreshJwt = this

                    require(this.parentJwtId == request.issuedJwt.jwtId) { "Tokens are not linked" }

                    if (this.isRevoked) {
                        errors.add(
                            create(
                                Error.INVALID_JWT, Field.FLD_ACCESS_JWT,
                                findI18nMessage(I18NEnum.FLD_JWT_IS_REVOKED, request.refreshToken.last15())
                            )
                        )
                    } else {

                        require(this.revokeDate == null) { "Tokens is revoked: ${this.revokeDate}" }

                        // get login from claims
                        jwtSecurityService.getClaimAbs(jwt, CL_USER)?.apply {
                            require(this == request.issuedJwt.issuedTo) { "Tokens owners are not linked" }
                        } ?: errors.add(
                            create(
                                Error.CLAIM_NOT_FOUND, Field.JWT_TOKEN_CLAIM,
                                findI18nMessage(I18NEnum.JWT_CLAIM_IS_MISSING, CL_USER, request.refreshToken.last15())
                            )
                        )
                    }
                } ?: errors.add(
                    create(
                        Error.INVALID_JWT, Field.FLD_ACCESS_JWT,
                        findI18nMessage(I18NEnum.FLD_INVALID_JWT, request.refreshToken.last15())
                    )
                )

            if (errors.isEmpty())
                request.apply {
                    loginService.login(request.login).apply {
                        if (isNotEmpty()) errors.addAll(this)
                    }
                }
        }
    }
}