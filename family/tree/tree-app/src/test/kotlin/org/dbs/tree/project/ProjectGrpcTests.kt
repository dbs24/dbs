package org.dbs.tree.project

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.dbs.tree.BaseTreeGrpcTest
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.tree.project.ProjectGrpcFuncs.buildProjectRequest
import org.dbs.tree.project.ProjectGrpcFuncs.createOrUpdateProjectSuccess
import org.dbs.tree.user.UserGrpcFuncs.buildUserRequest
import org.dbs.tree.user.UserGrpcFuncs.createOrUpdateUserSuccess
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Suppress("unused")
@Isolate
class ProjectsGrpcTests : BaseTreeGrpcTest() {

    private val newProjectShortName: String  get() = "validgrpcproject$testNum"
    private val newUserLogin: String  get() = "validgrpcpuser$testNum"

    init {

        val viaSource = " via $source"
        isolationMode = IsolationMode.InstancePerTest
        testCaseOrder = TestCaseOrder.Random
//        testExecutionMode = TestExecutionMode.Concurrent

        "Create project$viaSource" {

            val hotUserLogin = newUserLogin
            val user = createOrUpdateUserSuccess(buildUserRequest(hotUserLogin, "$hotUserLogin$TEST_MAIL_DOMAIN", "Strong12Password"))

            val hotProjectLogin = newProjectShortName

            createOrUpdateProjectSuccess(buildProjectRequest(hotProjectLogin, "${hotProjectLogin}Full", user.login))
        }

//        "Create another project$viaSource" {
//
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//        }
//
//        "Try to create invalid exists project$viaSource" {
//
//            val hotProjectLogin = newProjectShortName
//
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//
//            createOrUpdateProjectWithValidationError(
//                buildProjectRequest(login = hotProjectLogin, email = "$hotProjectLogin$TEST_MAIL_DOMAIN", password = "Strong12Password"),
//                ALREADY_EXISTS to SSS_USER_LOGIN
//            )
//        }
//
//        "Try to create invalid project$viaSource" {
//            createOrUpdateProjectWithValidationError(
//                buildProjectRequest(login = "", email = "", password = "fp"),
//                MANDATORY_FIELD_MISSING to SSS_USER_LOGIN
//            )
//        }
//
//        "Try to create invalid project with invalid login$viaSource" {
//            createOrUpdateProjectWithValidationError(
//                buildProjectRequest(login = "vali", email = "valid_project2$TEST_MAIL_DOMAIN", password = "Strong12Password"),
//                INVALID_ATTR_PATTERN_MISMATCH to SSS_USER_LOGIN
//            )
//        }
//
//        "Update project $viaSource" {
//
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//
//            createOrUpdateSuccess(buildProjectRequest(login = hotProjectLogin, oldLogin = hotProjectLogin,
//                email = "$hotProjectLogin$TEST_MAIL_DOMAIN", password = "Strong12Password", firstName = "firstName"))
//
//        }
//
//        "Get project1 credentials$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "ACTUAL"))
//            getProjectCredentials(buildProjectCredentialsRequest(hotProjectLogin))
//        }
//
//        "Get project credentials with fail$viaSource" {
//            getProjectCredentialsWithFails(
//                buildProjectCredentialsRequest("invalidgrpcproject0"),
//                USER_DOES_NOT_EXISTS to SSS_USER_LOGIN
//            )
//        }
//
//        "Get project credentials with invalid login$viaSource" {
//            getProjectCredentialsWithFails(
//                buildProjectCredentialsRequest("loginNotExists#1"),
//                INVALID_ATTR_PATTERN_MISMATCH to SSS_USER_LOGIN
//            )
//        }
//
//        "Get project2 credentials$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "ACTUAL"))
//            getProjectCredentials(buildProjectCredentialsRequest(hotProjectLogin))
//        }
//
//        "Try to close project with unknown status$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//
//            updateProjectStatusWithFail(
//                buildProjectStatusRequest(login = hotProjectLogin, newStatus = "FAKED_STATUS"),
//                UNKNOWN_ENTITY_STATUS to SSS_USER_STATUS
//            )
//        }
//
//        "Close project$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "CLOSED"))
//        }
//
//        "Close project2$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "CLOSED"))
//        }
//
//        "Try to close project with invalid status$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "CLOSED"))
//            updateProjectStatusWithFail(
//                buildProjectStatusRequest(login = hotProjectLogin, newStatus = "CLOSED"),
//                INVALID_ENTITY_STATUS to SSS_USER_STATUS
//            )
//        }
//
//        "Reopen project$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", "Strong12Password"))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "CLOSED"))
//
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "ACTUAL"))
//        }
//
//        "Update project password$viaSource" {
//            val hotProjectLogin = newProjectShortName
//            val password = "Strong12Password"
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", password))
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "ACTUAL"))
//
//            updateProjectPasswordSuccess(
//                buildProjectPasswordRequest(login = hotProjectLogin, oldP = password, newP = "Strong22Password")
//            )
//        }
//
//        "Try to update project with invalid password$viaSource" {
//
//            val hotProjectLogin = newProjectShortName
//            val password = "Strong12Password"
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", password))
//
//            updateProjectPasswordWithFail(
//                buildProjectPasswordRequest(login = hotProjectLogin, oldP = password+"1", newP = "Strong2Password"),
//                INVALID_OLD_ENTITY_PASSWORD to SSS_USER_OLD_PASSWORD
//            )
//        }
//
//        "Try to update project with invalid new password$viaSource" {
//
//            val hotProjectLogin = newProjectShortName
//            val password = "Strong13Password"
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", password))
//
//            updateProjectStatusSuccess(buildProjectStatusRequest(login = hotProjectLogin, newStatus = "CLOSED"))
//
//            updateProjectPasswordWithFail(
//                buildProjectPasswordRequest(login = hotProjectLogin, oldP = password, newP = password),
//                INVALID_ENTITY_OLD_AND_NEW_PASSWORD to SSS_USER_PASSWORD
//            )
//        }
//
//        "Try to update project password with invalid project status$viaSource" {
//
//            val hotProjectLogin = newProjectShortName
//            val password = "Strong13Password"
//            createOrUpdateSuccess(buildProjectRequest(hotProjectLogin, "$hotProjectLogin$TEST_MAIL_DOMAIN", password))
//
//            updateProjectPasswordWithFail(
//                buildProjectPasswordRequest(login = hotProjectLogin, oldP = password, newP = password),
//                INVALID_ENTITY_STATUS to SSS_USER_STATUS
//            )
//        }
        }
//    }
}
