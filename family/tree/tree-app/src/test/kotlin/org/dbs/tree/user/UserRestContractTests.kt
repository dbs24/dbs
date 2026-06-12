package org.dbs.tree.user

@Suppress("unused")
class UserRestContractTests : BaseTreeRestTest(), UserTestContract {

    override val userPrefix = "validrestuser"
    override val requestMapping = "/users"

    override suspend fun createUser(login: String, email: String, pass: String) {
        createUser(createUserDto(login, email, pass))
    }

    init {
        include(userTestsFactory(this))
    }
}
