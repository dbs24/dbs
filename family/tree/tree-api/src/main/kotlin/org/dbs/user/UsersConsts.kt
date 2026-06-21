package org.dbs.user

import org.dbs.consts.AnyCode
import org.dbs.consts.EntityId


typealias UserId = EntityId
typealias UserLogin = AnyCode
typealias UserPassword = String
object UsersConsts {

    object Names {
        const val USER = "User"
        const val PROJECT = "Project"
        const val GROUP = "Group"
    }

}
