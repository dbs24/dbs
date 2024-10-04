package org.dbs.rest.dto.login.exists

import org.dbs.spring.core.api.EntityInfo

data class CreatedLoginExists(
    val accessToken: String?,
    val userLogin: String?,
    val userLoginIsExists: Boolean = false
) : EntityInfo
