package org.dbs.tree.user

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.dbs.user.FamilyTreeCore.EntityStatus.ES_USER_ACTUAL
import org.dbs.user.dto.user.UpdateUserStatusDto
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class]
)
@Import(TreeConfig::class)
@Suppress("unused")
@Isolate
class SecurityRestTests : BaseTreeRestTest() {

    override val requestMapping = "/security"
    private val newUserLogin: String  get() = "validrestuser$testNum"

    init {

        isolationMode = IsolationMode.InstancePerTest
        testCaseOrder = TestCaseOrder.Random
        //testExecutionMode = TestExecutionMode.Concurrent

        "Login user via $source" {

            val hotUserLogin = newUserLogin
            val hotUserPassword = "rest_Strong1Password$hotUserLogin"

            // create user
            val dto = createUserDto(hotUserLogin, "$hotUserLogin@test.com", hotUserPassword)
            createUser( dto)

            // update status
            val updateDto = UpdateUserStatusDto(hotUserLogin, ES_USER_ACTUAL.entityStatusName)
            updateUserStatus(updateDto)

            // login
            val loginDto = createLoginUserDto(hotUserLogin, hotUserPassword)
            loginUser(loginDto)

        }

    }
}
