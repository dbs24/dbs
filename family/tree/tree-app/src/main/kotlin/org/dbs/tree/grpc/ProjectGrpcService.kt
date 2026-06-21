package org.dbs.tree.grpc

import org.dbs.tree.client.CreateOrUpdateProjectRequest
import org.dbs.tree.client.CreateOrUpdateProjectResponse
import org.dbs.tree.client.ProjectServiceGrpcKt
import org.dbs.tree.mapper.ProjectMappers.toCommand
import org.dbs.tree.mapper.ProjectMappers.toProjectProto
import org.dbs.tree.service.ProjectService
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class GrpcProjectController(val projectService: ProjectService) :
    ProjectServiceGrpcKt.ProjectServiceCoroutineImplBase() {

    override suspend fun createOrUpdateProject(request: CreateOrUpdateProjectRequest): CreateOrUpdateProjectResponse =
        projectService.createOrUpdateProject(request.toCommand()).toProjectProto()

}
