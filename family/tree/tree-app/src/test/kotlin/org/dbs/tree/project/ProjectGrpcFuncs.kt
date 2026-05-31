package org.dbs.tree.project

import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.dbs.test.ko.BaseSpec.PropertyValidator
import org.dbs.tree.BaseTreeGrpcTest
import org.dbs.tree.model.project.Project
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_PROJECT_ACTUAL
import org.dbs.user.FamilyTreeCore.UserActionEnum.EA_CREATE_OR_UPDATE_PROJECT
import org.dbs.tree.client.CreateOrUpdateProjectRequest as PROJECT

object ProjectGrpcFuncs {

    fun BaseTreeGrpcTest.buildProjectRequest(
        projectShortName: String, projectFullName: String?, owner: String,
    ): PROJECT = PROJECT.newBuilder().build {
        setProjectShortName(projectShortName)
        setProjectFullName(projectFullName)
        setProjectOwner(owner)
    }

    suspend fun BaseTreeGrpcTest.createOrUpdateProjectSuccess(req: PROJECT) =
        runCall { projectStub.createOrUpdateProject(req) }
            .shouldSuccess { res ->
                res.projectShortName shouldBe req.projectShortName
                res.status shouldBe ES_PROJECT_ACTUAL.entityStatusName

                val userOwnerId = userRepo.findByLogin(req.projectOwner)?.userId ?: error("owner not defined")

                val userValidators: Array<PropertyValidator<Project, *>> = arrayOf(
                    Project::entityStatus verify { it shouldBe ES_PROJECT_ACTUAL },
                    Project::projectId verify { it shouldBe entityId },
                    Project::entityId verify { it shouldBe projectId },
                    Project::closeDate verify { it shouldBe null },
                    Project::createDate verify { it shouldNotBe null },
                    Project::modifyDate verify { if (req.oldProjectShortName == "") it shouldBe createDate else it shouldBeGreaterThan createDate },
                    Project::shortName verify { it shouldBe req.projectShortName },
                    Project::fullName verify { it shouldBe req.projectFullName },
                    Project::ownerId verify { it shouldBe userOwnerId  },
                )

                verifyModifiedEntity(
                    projectRepo.findByShortName(req.projectShortName),
                    EA_CREATE_OR_UPDATE_PROJECT,
                    verifyAllFields = true,
                    *userValidators
                )
            }

}