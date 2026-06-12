package org.dbs.tree.user

import org.dbs.tree.BaseTreeGrpcTest
import org.dbs.tree.user.UserGrpcFuncs.buildUserRequest
import org.dbs.tree.user.UserGrpcFuncs.createOrUpdateUserSuccess


@Suppress("unused")
class UserGrpcContractTests : BaseTreeGrpcTest(), UserTestContract {

    override val userPrefix = "validgrpcuser"

    override suspend fun createUser(login: String, email: String, pass: String) {
        createOrUpdateUserSuccess(buildUserRequest(login, email, pass))
    }

    init {
        include(userTestsFactory(this))
    }
}
