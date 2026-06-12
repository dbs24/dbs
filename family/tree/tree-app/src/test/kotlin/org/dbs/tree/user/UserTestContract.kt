package org.dbs.tree.user

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.stringSpec
import org.dbs.test.ko.BaseSpec.Companion.testNum
import org.dbs.tree.TreeApplication
import org.dbs.tree.config.TreeConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TreeApplication::class],
)
@Import(TreeConfig::class)
@Isolate
interface UserTestContract {
    val userPrefix: String
    val source: String
    suspend fun createUser(login: String, email: String, pass: String)
    fun buildUserLogin() = "$userPrefix${testNum}"
}

fun userTestsFactory(contract: UserTestContract) = stringSpec {
    // Вычисляемое свойство внутри фабрики

    "Create user via ${contract.source}" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login@test.com", "Strong1Password")
    }

    "Create another user via ${contract.source}" {
        val login = contract.buildUserLogin()
        contract.createUser(login, "$login@test.com", "Strong12Password")
    }
}
