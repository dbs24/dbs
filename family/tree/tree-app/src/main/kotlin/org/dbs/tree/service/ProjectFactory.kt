package org.dbs.tree.service

import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.tree.model.user.User
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_PROJECT_ACTUAL
import org.springframework.stereotype.Component
import java.time.LocalDateTime.now
import org.dbs.tree.model.project.Project as ENTITY

@Component
class ProjectFactory: AbstractApplicationService() {

    fun createNewProject(shortName: String, owner: User, fullName: String?): ENTITY = now().let {
        ENTITY(
            shortName = shortName,
            fullName = fullName,
            ownerId = owner.entityId ?: error("owner not defined"),
            entityStatus = ES_PROJECT_ACTUAL,
            createDate = it,
            modifyDate = it,
            closeDate = null,
        )
    }
}
