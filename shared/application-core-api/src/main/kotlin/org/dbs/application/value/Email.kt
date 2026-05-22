package org.dbs.application.value

import org.dbs.application.core.service.funcs.Patterns.EMAIL_PATTERN


@JvmInline
value class Email(override val value: String): StringValue {

    init {
        require(value.length <= 64) { "Email length too long" }
        require(EMAIL_PATTERN.toRegex().matches(value)) { "Invalid email format" }
    }

    override fun toString() = value

}
