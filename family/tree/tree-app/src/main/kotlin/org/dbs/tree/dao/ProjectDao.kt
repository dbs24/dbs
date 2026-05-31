package org.dbs.tree.dao

import org.dbs.consts.Login
import org.dbs.service.cache.EntityIdCacheService
import org.dbs.service.cache.v2.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.tree.repo.ProjectRepo
import org.dbs.user.FamilyTreeCore.CacheKeyUserEnum.FT_PROJECT_CODE
import org.dbs.user.FamilyTreeCore.CacheKeyUserEnum.FT_PROJECT_ID
import org.springframework.stereotype.Service
import org.dbs.tree.model.project.Project as ENTITY

@Service
class ProjectDao(
    val projectRepo: ProjectRepo,
    val entityIdCacheService: EntityIdCacheService,
    val entityCacheService: EntityCacheService<ENTITY>,
) : DaoAbstractApplicationService() {

    suspend fun saveProject(project: ENTITY) : ENTITY = projectRepo.save(project)

    suspend fun findProjectByShortName(shortName: String): ENTITY? =
        entityCacheService.getEntity(FT_PROJECT_CODE, shortName) {
            projectRepo.findByShortName(shortName)
        }


    fun invalidateCaches(projectLogin: Login)  {
            entityCacheService.invalidateCaches(projectLogin, FT_PROJECT_CODE)
            entityIdCacheService.invalidateCaches(projectLogin, FT_PROJECT_ID)
        }
}
