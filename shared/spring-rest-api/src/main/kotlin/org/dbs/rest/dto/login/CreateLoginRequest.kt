package org.dbs.rest.dto.login


import org.dbs.spring.core.api.PostRequestBody
import org.dbs.consts.SysConst.VERSION_1_0_0
import org.dbs.rest.api.action.SimpleActionInfo

data class CreateLoginRequest(
    override val version: String = VERSION_1_0_0,
    override val entityAction: SimpleActionInfo,
    override val entityInfo: LoginInfo
) : PostRequestBody
