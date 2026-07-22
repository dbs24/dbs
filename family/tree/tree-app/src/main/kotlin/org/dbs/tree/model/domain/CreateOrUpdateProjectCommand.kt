package org.dbs.tree.model.domain

 import org.dbs.rest.api.nio.DomainCommand
 import org.dbs.tree.model.project.Project
 import org.dbs.tree.model.user.User
 import org.dbs.utils.lateInitProperty

data class CreateOrUpdateProjectCommand(
    val oldProjectShortName: String?,
    val projectShortName: String,
    val projectFullName: String?,
    val projectOwner: String
) : DomainCommand {

    var owner: User by lateInitProperty()
    var updated: Project by lateInitProperty()

    val isNewProject: Boolean = oldProjectShortName == null
    val isUpdateProject: Boolean = !isNewProject && (oldProjectShortName != projectShortName)
}
