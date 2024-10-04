package org.dbs.rest.dto.registration

import org.dbs.spring.core.api.EntityInfo
import org.dbs.consts.SysConst.STRING_NULL

data class CreatedRegistration(var activationCode: String? = STRING_NULL) : EntityInfo
