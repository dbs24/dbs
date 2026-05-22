package org.dbs.application.value

import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN


@JvmInline
value class UserLogin(override val value: String): StringValue {

    init {
        require(value.length <= 32) { "User login too long" }
        require(LOGIN_PATTERN.toRegex().matches(value)) { "Invalid user login format" }
    }

    override fun toString() = value

}
