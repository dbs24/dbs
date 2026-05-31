package org.dbs.tree.service

import org.dbs.entity.core.v2.model.LogEntityAction
import org.dbs.rest.validation.ValidateDto
import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.tree.model.domain.CreateOrUpdateProjectCommand
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_PROJECT_ACTUAL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime.now
import org.dbs.tree.dao.ProjectDao as DAO
import org.dbs.tree.model.project.Project as ENTITY

@Service
class ProjectService(
    val dao: DAO,
    val project: ProjectFactory,
) : AbstractApplicationService() {

    @ValidateDto
    @LogEntityAction("EA_CREATE_OR_UPDATE_PROJECT")
    @Transactional
    suspend fun createOrUpdateProject(request: CreateOrUpdateProjectCommand): ENTITY {

        val updatedProject = if (request.isNewProject) project.createNewProject(request.projectShortName, request.owner, request.projectFullName)
        else (request.updated)

        return dao.saveProject(
            updatedProject.copy(
                shortName = request.projectShortName,
                fullName = request.projectFullName,
                ownerId = request.owner.entityId ?: error("owner not defined"),
                entityStatus = ES_PROJECT_ACTUAL.takeIf { request.isNewProject } ?: updatedProject.status,
                modifyDate = if (!request.isNewProject) now() else updatedProject.modifyDate,
                closeDate = updatedProject.closeDate
            ))
    }

    suspend fun findProjectByShortname(projectShortName: String): ENTITY? =
        dao.findProjectByShortName(projectShortName.also { logger.debug { "find project login: $projectShortName" } })


}
