package org.dbs.rest.dto.registration

import org.dbs.spring.core.api.EntityInfo


class RegistrationInfo(
    val accessToken: String,
    val userLogin: String,
    val userPass: String
) : EntityInfo
