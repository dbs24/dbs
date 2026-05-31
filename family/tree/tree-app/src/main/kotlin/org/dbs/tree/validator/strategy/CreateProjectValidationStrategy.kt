package org.dbs.tree.validator.strategy

import org.dbs.application.core.service.funcs.Patterns.COMMON_DESCRIPTION
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN
import org.dbs.enums.I18NEnum
import org.dbs.rest.validation.FieldValidationRule
import org.dbs.rest.validation.ValidationStrategy
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.tree.service.ProjectService
import org.dbs.tree.service.UserService
import org.dbs.validator.Error
import org.dbs.validator.Error.ALREADY_EXISTS
import org.dbs.validator.ErrorInfo.Companion.create
import org.dbs.validator.Field.FLD_DESCRIPTION
import org.dbs.validator.Field.SSS_PROJECT_SHORT_NAME
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.validator.Field.SSS_USER_OLD_LOGIN
import org.springframework.stereotype.Component
import org.dbs.tree.model.domain.CreateOrUpdateProjectCommand as DTO

@Component
class CreateProjectValidationStrategy(
    private val userService: UserService,
    private val projectService: ProjectService,
) : ValidationStrategy<DTO> {

    override val supportedClass = DTO::class

    override val rules: Collection<FieldValidationRule<DTO>> = listOf(
        DTO::oldProjectShortName matches (LOGIN_PATTERN to SSS_USER_OLD_LOGIN),
        DTO::projectShortName matches (LOGIN_PATTERN to SSS_USER_LOGIN),
        DTO::projectFullName matches (COMMON_DESCRIPTION to FLD_DESCRIPTION),
        DTO::projectOwner matches (LOGIN_PATTERN to SSS_USER_OLD_LOGIN),
    )

    override fun validate(request: DTO) {
        validateInternal(request) { errors ->

            request.apply {

                val project = projectService.findProjectByShortname(projectShortName)

                // new project should not exist
                if (isNewProject || isUpdateProject) {

                    project?.apply {  errors.add(
                            create(
                                ALREADY_EXISTS, SSS_PROJECT_SHORT_NAME,
                                findI18nMessage(I18NEnum.EXIST_PROJECT_SHORT_NAME)
                            ))   }
                } else {

                    project?.apply {
                        request.updated = this
                    } ?:
                    errors.add(
                        create(
                            Error.ENTITY_NOT_FOUND, SSS_PROJECT_SHORT_NAME,
                            findI18nMessage(I18NEnum.ENTITY_NOT_FOUND_WITH_ID, projectShortName)
                        ))
                }

                userService.findUserByLogin(projectOwner)?.apply {
                    request.owner = this
                } ?:
                errors.add(
                    create(
                        Error.ENTITY_NOT_FOUND, SSS_USER_LOGIN,
                        findI18nMessage(I18NEnum.ENTITY_NOT_FOUND_WITH_ID, projectOwner)
                    ))
            }
        }
    }
}
