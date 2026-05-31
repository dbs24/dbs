package org.dbs.tree.mapper

import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.tree.client.CreateOrUpdateProjectRequest
import org.dbs.tree.client.CreateOrUpdateProjectResponse
import org.dbs.tree.model.domain.CreateOrUpdateProjectCommand
import org.dbs.tree.model.project.Project


object ProjectMappers {

    fun CreateOrUpdateProjectRequest.toCommand(): CreateOrUpdateProjectCommand =
        CreateOrUpdateProjectCommand(
            oldProjectShortName = oldProjectShortName.grpcGetOrNull(),
            projectShortName = projectShortName,
            projectFullName = projectFullName.grpcGetOrNull(),
            projectOwner = projectOwner

        )

    fun Project.toProjectProto() : CreateOrUpdateProjectResponse = CreateOrUpdateProjectResponse.newBuilder()
        .also {
            it.projectShortName = shortName
            it.status = status.entityStatusName
        }.build()
}