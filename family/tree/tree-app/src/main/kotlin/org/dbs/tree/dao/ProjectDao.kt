package org.dbs.tree.dao

import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.tree.repo.ProjectRepo
import org.springframework.stereotype.Service
import org.dbs.tree.model.project.Project as ENTITY

@Service
class ProjectDao(
    val projectRepo: ProjectRepo,
) : DaoAbstractApplicationService() {

    suspend fun saveProject(project: ENTITY) : ENTITY = projectRepo.save(project)

    suspend fun findProjectByShortName(shortName: String): ENTITY? =
            projectRepo.findByShortName(shortName)

}
